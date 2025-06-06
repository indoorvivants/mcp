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

// package mcp

// import java.io.BufferedReader
// import java.io.InputStream
// import java.io.InputStreamReader
// import java.io.OutputStream
// import java.util.concurrent.CompletableFuture
// import java.util.concurrent.ConcurrentHashMap
// import java.util.concurrent.Executor
// import java.util.concurrent.Executors
// import java.util.concurrent.TimeUnit
// import java.util.concurrent.atomic.AtomicInteger

// import scala.concurrent.ExecutionContext
// import scala.concurrent.Future

// import mcp.*
// import mcp.json.*
// import upickle.core.TraceVisitor.TraceException

// class AsyncTransport private (opts: AsyncTransport.Opts)
//     extends Transport[Future[Unit]]:

//   override type F[A] = Future[A]

//   /** Enable logging request/response bodies to STDERR */
//   def verbose: AsyncTransport = copy(_.copy(log = true))

//   /** Change default executor used for running handlers. Using a single threaded
//     * executor is NOT recommended.
//     */
//   def executor(e: Executor): AsyncTransport = copy(_.copy(executor = e))

//   /** Change default output stream used for writing responses */
//   def out(os: OutputStream): AsyncTransport = copy(_.copy(out = os))

//   /** Change default input stream used for reading requests/notifications */
//   def in(is: InputStream): AsyncTransport = copy(_.copy(in = is))

//   /** Attach to input and output streams, processing requests and BLOCKING until
//     * input stream is closed
//     */
//   override def run(endpoints: ServerEndpoints[Future]): Future[Unit] =
//     val reader = new BufferedReader(new InputStreamReader(opts.in))
//     val pending =
//       new ConcurrentHashMap[RpcId, CompletableFuture[ResponseParams]]

//     given ec: ExecutionContext = ExecutionContext.fromExecutor(opts.executor)

//     val comm: ServerToClient[Id] = new:
//       override def request[X <: MCPRequest & FromServer](
//           req: X,
//           options: RequestOptions = RequestOptions.default
//       )(in: req.In): req.Out | Error =
//         val id = genId()
//         val params = writeJs(in)
//         params("id") = id
//         out(params) // Write the request parameters to the output stream
//         val fut = new CompletableFuture[ResponseParams]()
//         pending.put(id, fut)
//         options.timeout.foreach: dur =>
//           fut.completeOnTimeout(ujson.Null, dur.toMillis, TimeUnit.MILLISECONDS)
//         val response = fut.get() // N.B.: will block until response is received
//         if response == ujson.Null then
//           Error(
//             ErrorCode.InternalError,
//             s"Timed out after waiting for response for request $id"
//           )
//         else if response.obj.contains("error") then
//           read[Error](response("error"))
//         else read[req.Out](response("result"))
//         end if

//       end request

//       override def notification[X <: MCPNotification & FromServer](notif: X)(
//           in: notif.In
//       ): Unit = out(in)
//     end comm

//     def processClientResponse(id: RpcId, response: ResponseParams) =
//       val n = pending.remove(id)
//       if n != null then n.complete(response)
//       else err(s"Received a response $response for an unknown request $id")

//     given ServerToClient[Future]:
//       override def notification[X <: MCPNotification & FromServer](notif: X)(
//           in: notif.In
//       ): Future[Unit] = Future(comm.notification[X](notif)(in))

//       override def request[X <: MCPRequest & FromServer](
//           req: X,
//           options: RequestOptions
//       )(in: req.In): Future[req.Out | Error] = Future(
//         comm.request(req, options)(in)
//       )
//     end given

//     Future:
//       var line: String = null
//       while { line = reader.readLine(); line != null } do
//         opts.executor.execute(() =>
//           handleLine(
//             line,
//             processClientResponse,
//             endpoints
//           )
//         )
//       end while

//   end run

//   protected opaque type RpcId <: ujson.Value = ujson.Value
//   protected opaque type ResponseParams <: ujson.Value = ujson.Value

//   private val serverRequestCounter = new AtomicInteger(1)

//   private def genId() = ujson.Num(serverRequestCounter.getAndIncrement())

//   private def out[J: Writer](j: J) =
//     val obj = writeJs(j)
//     obj("jsonrpc") = "2.0"

//     def dropNulls(v: ujson.Value): ujson.Value =
//       v match
//         case ujson.Obj(fields) =>

//           val v: ujson.Obj = fields.flatMap: (k, v) =>
//             if v == ujson.Null then None
//             else Some(k -> dropNulls(v))

//           v

//         case ujson.Arr(items) =>
//           items.map(dropNulls): ujson.Arr

//         case _ => v
//     end dropNulls

//     if opts.log then err(s"Outputting ${dropNulls(obj)}")

//     output(write(dropNulls(obj)))
//   end out

//   private def handleLine(
//       line: String,
//       processClientResponse: (RpcId, ResponseParams) => Unit,
//       endpoints: ServerEndpoints[Future]
//   )(using ServerToClient[Future], ExecutionContext) =
//     try
//       val json = read[ujson.Obj](line)
//       if opts.log then err(s"Receiving $json")
//       (
//         hasId = json.value.contains("id"),
//         hasMethod = json.value.contains("method")
//       ) match
//         case (hasId = true, hasMethod = true) =>
//           val method = json.value("method").str
//           val id = json.value("id")

//           val response: Future[Response] =
//             handleExceptions(id):
//               endpoints.requestHandlers.get(method) match
//                 case None =>
//                   Future.successful(
//                     Response(
//                       id,
//                       error = Some(
//                         Error(
//                           ErrorCode.MethodNotFound,
//                           s"Method $method is not handled"
//                         )
//                       )
//                     )
//                   )

//                 case Some(handler) =>
//                   handler(json).map:
//                     case err: Error =>
//                       Response(id, error = Some(err))

//                     case response: ujson.Value =>
//                       Response(id, result = Some(writeJs(response)))

//           response.foreach(out)

//         case (hasId = false, hasMethod = true) =>
//           val method = json.value("method").str

//           endpoints.notificationHandlers.get(method) match
//             case None        => // do nothing
//             case Some(value) => value(json)

//         case (hasId = true, hasMethod = false) =>
//           // it's a response from client
//           processClientResponse(json.value("id"), json)

//         case _ =>
//           err(
//             s"Failed to interpret JSON ($line) as request/response/notification"
//           )

//       end match
//     catch
//       case exc =>
//         err(s"Failed to parse JSON ($line): ${exc.getMessage}")

//   private def err(msg: String) =
//     this.synchronized:
//       opts.err.write(msg.getBytes)
//       opts.err.write('\n')

//   private def output(msg: String) =
//     this.synchronized:
//       opts.out.write(msg.getBytes)
//       opts.out.write('\n')

//   private def handleExceptions[T](id: ujson.Value)(using
//       ExecutionContext
//   )(f: => Future[Response]) =
//     try f
//     catch
//       case e: TraceException =>
//         Future.successful(
//           Response(
//             id,
//             error = Some(Error(ErrorCode.InvalidRequest, e.getMessage))
//           )
//         )
//       case e =>
//         Future.successful(
//           Response(
//             id,
//             error = Some(Error(ErrorCode.InternalError, e.getMessage))
//           )
//         )

//   private def copy(
//       f: AsyncTransport.Opts => AsyncTransport.Opts
//   ): AsyncTransport =
//     new AsyncTransport(f(opts))
// end AsyncTransport

// object AsyncTransport:
//   val default = new AsyncTransport(
//     Opts(
//       false,
//       ExecutionContext.global,
//       System.in,
//       System.out,
//       System.err
//     )
//   )

//   def apply(): AsyncTransport = default

//   private case class Opts(
//       log: Boolean,
//       executor: Executor,
//       in: InputStream,
//       out: OutputStream,
//       err: OutputStream
//   )
// end AsyncTransport
