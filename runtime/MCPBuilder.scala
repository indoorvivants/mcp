package mcp

import mcp.json.*
import upickle.core.TraceVisitor.TraceException

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import MCPBuilder.Opts
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

trait Communicate:
  def notification[X <: MCPNotification](notif: X, in: notif.In): Unit
  def request[X <: MCPRequest](req: X, in: req.In): req.Out | Error

  def request[X <: MCPRequest](req: PreparedRequest[X]): req.Out | Error =
    this.request[X](req.x, req.in)

  def notification[X <: MCPNotification](
      req: PreparedNotification[X]
  ): Unit =
    this.notification[X](req.x, req.in)
end Communicate

inline def communicate(using comm: Communicate) = comm

class PreparedRequest[X <: MCPRequest](val x: X, val in: x.In):
  type Out = x.Out

class PreparedNotification[X <: MCPNotification](val x: X, val in: x.In)

class MCPBuilder private (opts: Opts):
  private def copy(f: Opts => Opts) = new MCPBuilder(f(opts))

  def verbose: MCPBuilder = copy(_.copy(log = true))

  def executor(e: Executor): MCPBuilder = copy(_.copy(executor = e))

  def out(os: OutputStream): MCPBuilder = copy(_.copy(out = os))

  def in(is: InputStream): MCPBuilder = copy(_.copy(in = is))

  def handleRequest(req: MCPRequest & FromClient)(
      f: Communicate ?=> req.In => req.Out | Error
  ): MCPBuilder =
    val handler = (c: Communicate) ?=>
      (in: ujson.Value) =>
        val params = read[req.In](in)
        f(params) match
          case e: Error => writeJs(e)
          case e        => writeJs(e.asInstanceOf[req.Out])

    copy(o =>
      o.copy(requestHandlers = o.requestHandlers.updated(req.method, handler))
    )
  end handleRequest

  def handleNotification(req: MCPNotification & FromClient)(
      f: Communicate ?=> req.In => Unit
  ): MCPBuilder =
    val handler = (c: Communicate) ?=>
      (in: ujson.Value) =>
        val params = read[req.In](in)
        f(params)

    copy(o =>
      o.copy(notificationHandlers =
        o.notificationHandlers.updated(req.method, handler)
      )
    )
  end handleNotification

  protected opaque type Id <: ujson.Value = ujson.Value
  protected opaque type ResponseParams <: ujson.Value = ujson.Value

  private val serverRequestCounter = new AtomicInteger(1)

  private def genId() = ujson.Num(serverRequestCounter.getAndIncrement())

  def run(): Unit =
    val reader = new BufferedReader(new InputStreamReader(opts.in))
    val pending = new ConcurrentHashMap[Id, CompletableFuture[ResponseParams]]

    given Communicate:
      override def request[X <: MCPRequest](req: X, in: req.In): req.Out |
        Error =
        val id = genId()
        val params = writeJs(in)
        params("id") = id
        out(params) // Write the request parameters to the output stream
        val fut = new CompletableFuture[ResponseParams]()
        pending.put(id, fut)
        fut.completeOnTimeout(ujson.Null, 30000, TimeUnit.MILLISECONDS)
        val response = fut.get() // N.B.: will block until response is received
        if response == ujson.Null then
          Error(
            ErrorCode.InternalError,
            s"Timed out after waiting for response for request $id"
          )
        else if response.obj.contains("error") then
          read[Error](response("error"))
        else read[req.Out](response("result"))
        end if

      end request

      override def notification[X <: MCPNotification](
          notif: X,
          in: notif.In
      ): Unit = out(in)
    end given

    def processClientResponse(id: Id, response: ResponseParams) =
      pending.remove(id).complete(response)

    var line: String = null
    while { line = reader.readLine(); line != null } do
      opts.executor.execute(() => handleLine(line, processClientResponse))
    end while
  end run

  private def out[J: Writer](j: J) =
    val obj = writeJs(j)
    obj("jsonrpc") = "2.0"

    def dropNulls(v: ujson.Value): ujson.Value =
      v match
        case ujson.Obj(fields) =>

          val v: ujson.Obj = fields.flatMap: (k, v) =>
            if v == ujson.Null then None
            else Some(k -> dropNulls(v))

          v

        case ujson.Arr(items) =>
          items.map(dropNulls): ujson.Arr

        case _ => v
    end dropNulls

    if opts.log then err(s"Outputting ${dropNulls(obj)}")

    output(write(dropNulls(obj)))
  end out

  private def handleLine(
      line: String,
      processClientResponse: (Id, ResponseParams) => Unit
  )(using Communicate) =
    try
      val json = read[ujson.Obj](line)
      if opts.log then err(s"Receiving $json")
      (
        hasId = json.value.contains("id"),
        hasMethod = json.value.contains("method")
      ) match
        case (hasId = true, hasMethod = true) =>
          val method = json.value("method").str
          val id = json.value("id")

          val response =
            handleExceptions(id):
              opts.requestHandlers.get(method) match
                case None =>
                  Response(
                    id,
                    error = Some(
                      Error(
                        ErrorCode.MethodNotFound,
                        s"Method $method is not handled"
                      )
                    )
                  )

                case Some(handler) =>
                  handler(json) match
                    case err: Error =>
                      Response(id, error = Some(err))

                    case response: ujson.Value =>
                      Response(id, result = Some(writeJs(response)))

          out(response)

        case (hasId = false, hasMethod = true) =>
          val method = json.value("method").str

          opts.notificationHandlers.get(method) match
            case None        => // do nothing
            case Some(value) => value(json)

        case (hasId = true, hasMethod = false) =>
          // it's a response from client
          processClientResponse(json.value("id"), json)

        case _ =>
          err(
            s"Failed to interpret JSON ($line) as request/response/notification"
          )

      end match
    catch
      case exc =>
        err(s"Failed to parse JSON ($line): ${exc.getMessage}")

  private def err(msg: String) =
    this.synchronized:
      opts.err.write(msg.getBytes)
      opts.err.write('\n')

  private def output(msg: String) =
    this.synchronized:
      opts.out.write(msg.getBytes)
      opts.out.write('\n')

  private def handleExceptions[T](id: ujson.Value)(f: => Response) =
    try f
    catch
      case e: TraceException =>
        Response(
          id,
          error = Some(Error(ErrorCode.InvalidRequest, e.getMessage))
        )
      case e =>
        Response(
          id,
          error = Some(Error(ErrorCode.InternalError, e.getMessage))
        )

end MCPBuilder

object MCPBuilder:
  def create(): MCPBuilder =
    new MCPBuilder(
      Opts(
        Map.empty,
        Map.empty,
        false,
        ExecutionContext.global,
        System.in,
        System.out,
        System.err
      )
    )
  end create

  private case class Opts(
      requestHandlers: Map[
        String,
        Communicate ?=> (ujson.Value) => ujson.Value | Error
      ],
      notificationHandlers: Map[
        String,
        Communicate ?=> (ujson.Value) => Unit
      ],
      log: Boolean,
      executor: Executor,
      in: InputStream,
      out: OutputStream,
      err: OutputStream
  )
end MCPBuilder
