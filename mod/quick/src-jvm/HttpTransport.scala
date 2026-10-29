/*
 * Copyright 2025 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcp

import java.io.OutputStream
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

import scala.util.control.NonFatal

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import mcp.*
import mcp.internal.BuildInfo
import mcp.json.*
import upickle.core.TraceVisitor.TraceException

/** A synchronous HTTP transport for MCP implementing the "Streamable HTTP"
  * transport (spec 2026-07-28) — MVP subset: POST requests only, JSON
  * responses. Does NOT open SSE streams and does NOT support server-initiated
  * requests or notifications.
  *
  * Backed by the JDK built-in [[com.sun.net.httpserver.HttpServer]]. JVM-only.
  */
class HttpTransport private (opts: HttpTransport.Opts) extends Transport[Unit]:

  override type F[A] = Id[A]

  def verbose: HttpTransport = copy(_.copy(log = true))
  def host(h: String): HttpTransport = copy(_.copy(host = h))
  def port(p: Int): HttpTransport = copy(_.copy(port = p))
  def path(p: String): HttpTransport = copy(_.copy(path = p))
  def executor(e: Executor): HttpTransport = copy(_.copy(executor = e))
  def err(os: OutputStream): HttpTransport = copy(_.copy(err = os))
  def allowedOrigins(o: Set[String]): HttpTransport =
    copy(_.copy(allowedOrigins = o))

  /** Start the HTTP server and BLOCK the caller until interrupted. */
  override def run(endpoints: ServerEndpoints[Id]): Unit =
    val server = start(endpoints)
    val latch = new CountDownLatch(1)
    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      server.stop(0)
      latch.countDown()
    ))
    try latch.await()
    catch case _: InterruptedException => server.stop(0)
  end run

  def handler(endpoints: ServerEndpoints[Id]): HttpHandler = 
    new McpHandler(endpoints)

  /** Start the server without blocking. Returns the running [[HttpServer]] so
    * the caller can query the bound port or stop it. Useful for tests.
    */
  def start(endpoints: ServerEndpoints[Id]): HttpServer =
    val server = HttpServer.create(new InetSocketAddress(opts.host, opts.port), 0)
    server.createContext(opts.path, new McpHandler(endpoints))
    server.setExecutor(opts.executor)
    server.start()
    log(s"HTTP transport listening on ${opts.host}:${server.getAddress.getPort}${opts.path}")
    server
  end start

  protected opaque type RpcId <: ujson.Value = ujson.Value

  private final class Session(val id: String):
    val pending: ConcurrentHashMap[String, ujson.Value] =
      new ConcurrentHashMap[String, ujson.Value]

  private val sessions = new ConcurrentHashMap[String, Session]

  private val InitializeMethod = "initialize"

  private val noopServerToClient: ServerToClient[Id] = new:
    private def unsupported[A]: A | Error =
      Error(
        ErrorCode.InternalError,
        "server-initiated requests/notifications are not supported by HttpTransport (SSE disabled)"
      )
    override def request[X <: MCPRequest & FromServer](
        req: X,
        options: RequestOptions = RequestOptions.default
    )(in: req.In): req.Out | Error = unsupported
    override def notification[X <: MCPNotification & FromServer](notif: X)(
        in: notif.In
    ): Unit = ()

  private final class McpHandler(endpoints: ServerEndpoints[Id])
      extends HttpHandler:

    override def handle(exchange: HttpExchange): Unit =
      try
        exchange.getRequestMethod.toUpperCase match
          case "POST"   => handlePost(exchange)
          case "DELETE" => handleDelete(exchange)
          case "GET" =>
            // SSE not supported in MVP
            respondEmpty(exchange, 405)
          case _ => respondEmpty(exchange, 405)
      catch
        case NonFatal(e) =>
          log(s"Unhandled error: ${e.getMessage}")
          try respondEmpty(exchange, 500)
          catch case NonFatal(_) => ()
      finally exchange.close()
    end handle

    private def handlePost(exchange: HttpExchange): Unit =
      if !checkOrigin(exchange) then respondEmpty(exchange, 403)
      else
        val headers = exchange.getRequestHeaders
        val accept = Option(headers.getFirst("Accept")).getOrElse("")

        if !(accept.contains("application/json") && accept.contains("text/event-stream"))
        then respondEmpty(exchange, 406)
        else
          val bodyStr = new String(
            exchange.getRequestBody.readAllBytes(),
            StandardCharsets.UTF_8
          )
          try dispatch(exchange, headers, read[ujson.Obj](bodyStr))
          catch
            case NonFatal(e) =>
              log(s"Failed to parse body: ${e.getMessage}")
              respondEmpty(exchange, 400)
    end handlePost

    private def dispatch(
        exchange: HttpExchange,
        headers: com.sun.net.httpserver.Headers,
        json: ujson.Obj
    ): Unit =
      val method = json.value.get("method").map(_.str)
      val isInitialize = method.contains(InitializeMethod)

      val protocolOk =
        isInitialize || Option(headers.getFirst("MCP-Protocol-Version"))
          .getOrElse(BuildInfo.mcpProtocolVersion) == BuildInfo.mcpProtocolVersion

      if !protocolOk then
        log(s"Unsupported MCP protocol version")
        respondEmpty(exchange, 400)
      else
        val sessionResult: Either[Int, Option[Session]] =
          if isInitialize then Right(None)
          else
            Option(headers.getFirst("Mcp-Session-Id"))
              .toRight(400)
              .flatMap: sid =>
                Option(sessions.get(sid)).map(Some(_)).toRight(404)

        sessionResult match
          case Left(status) => respondEmpty(exchange, status)
          case Right(_)     => handleRpc(exchange, json)
    end dispatch

    private def handleRpc(exchange: HttpExchange, json: ujson.Obj): Unit =
      given ServerToClient[Id] = noopServerToClient

      val hasId = json.value.contains("id")
      val hasMethod = json.value.contains("method")

      (hasId, hasMethod) match
        case (true, true) =>
          val id = json.value("id")
          val m = json.value("method").str
          val params = json.value.getOrElse("params", ujson.Obj())

          val response = handleExceptions(id):
            endpoints.requestHandlers.get(m) match
              case None =>
                Response(
                  id,
                  error = Some(
                    Error(ErrorCode.MethodNotFound, s"Method $m is not handled")
                  )
                )
              case Some(handler) =>
                handler(params) match
                  case err: Error     => Response(id, error = Some(err))
                  case r: ujson.Value => Response(id, result = Some(writeJs(r)))

          val obj = writeJs(response)
          obj("jsonrpc") = "2.0"

          if m == InitializeMethod then
            val sid = UUID.randomUUID().toString
            sessions.put(sid, new Session(sid))
            exchange.getResponseHeaders.add("Mcp-Session-Id", sid)

          respondJson(exchange, 200, dropNulls(obj))

        case (false, true) =>
          val m = json.value("method").str
          val params = json.value.getOrElse("params", ujson.Obj())
          endpoints.notificationHandlers.get(m).foreach: h =>
            try h(params)
            catch case NonFatal(e) => log(s"Notification handler $m failed: ${e.getMessage}")
          respondEmpty(exchange, 202)

        case (true, false) =>
          respondEmpty(exchange, 202)

        case _ =>
          respondEmpty(exchange, 400)
    end handleRpc

    private def handleDelete(exchange: HttpExchange): Unit =
      if !checkOrigin(exchange) then respondEmpty(exchange, 403)
      else
        Option(exchange.getRequestHeaders.getFirst("Mcp-Session-Id")) match
          case None => respondEmpty(exchange, 400)
          case Some(sid) =>
            Option(sessions.remove(sid)) match
              case None    => respondEmpty(exchange, 404)
              case Some(_) => respondEmpty(exchange, 204)
    end handleDelete

    private def checkOrigin(exchange: HttpExchange): Boolean =
      Option(exchange.getRequestHeaders.getFirst("Origin")) match
        case None      => true // non-browser client
        case Some(org) => opts.allowedOrigins.contains(org)
  end McpHandler

  private def handleExceptions(id: ujson.Value)(f: => Response): Response =
    try f
    catch
      case e: TraceException =>
        Response(id, error = Some(Error(ErrorCode.InvalidRequest, e.getMessage)))
      case NonFatal(e) =>
        Response(id, error = Some(Error(ErrorCode.InternalError, e.getMessage)))

  private def dropNulls(v: ujson.Value): ujson.Value =
    v match
      case ujson.Obj(fields) =>
        val out: ujson.Obj = fields.flatMap: (k, vv) =>
          if vv == ujson.Null then None else Some(k -> dropNulls(vv))
        out
      case ujson.Arr(items) => items.map(dropNulls): ujson.Arr
      case _                => v

  private def respondJson(
      exchange: HttpExchange,
      status: Int,
      body: ujson.Value
  ): Unit =
    val bytes = write(body).getBytes(StandardCharsets.UTF_8)
    log(s"HTTP $status ${new String(bytes, StandardCharsets.UTF_8)}")
    exchange.getResponseHeaders.add("Content-Type", "application/json")
    exchange.sendResponseHeaders(status, bytes.length.toLong)
    val os = exchange.getResponseBody
    os.write(bytes)
    os.flush()

  private def respondEmpty(exchange: HttpExchange, status: Int): Unit =
    log(s"HTTP $status (empty)")
    exchange.sendResponseHeaders(status, -1)

  private def log(msg: String): Unit =
    if opts.log then
      this.synchronized:
        opts.err.write(msg.getBytes(StandardCharsets.UTF_8))
        opts.err.write('\n')
        opts.err.flush()

  private def copy(f: HttpTransport.Opts => HttpTransport.Opts): HttpTransport =
    new HttpTransport(f(opts))
end HttpTransport

object HttpTransport:
  // Default: allow no Origin-bearing clients. Non-browser clients (no Origin
  // header) always pass. Callers add origins via .allowedOrigins for browser use.
  private val defaultAllowedOrigins: Set[String] = Set.empty

  private def defaultExecutor(): Executor =
    try Executors.newVirtualThreadPerTaskExecutor()
    catch case NonFatal(_) => Executors.newCachedThreadPool()

  val default: HttpTransport = new HttpTransport(
    Opts(
      log = false,
      executor = defaultExecutor(),
      host = "127.0.0.1",
      port = 8080,
      path = "/mcp",
      err = System.err,
      allowedOrigins = defaultAllowedOrigins
    )
  )

  def apply(): HttpTransport = default

  private case class Opts(
      log: Boolean,
      executor: Executor,
      host: String,
      port: Int,
      path: String,
      err: OutputStream,
      allowedOrigins: Set[String]
  )
end HttpTransport
