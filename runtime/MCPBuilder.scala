package mcp

import mcp.json.*
import java.io.{BufferedReader, InputStreamReader}
import upickle.core.TraceVisitor.TraceException

class MCPBuilder private (
    requestHandlers: Map[
      String,
      (ujson.Value) => ujson.Value | Error
    ],
    notificationHandlers: Map[
      String,
      (ujson.Value) => Unit
    ]
):
  def handleRequest(req: MCPRequest & FromClient)(
      f: req.In => req.Out | Error
  ) =
    val handler = (in: ujson.Value) =>
      val params = read[req.In](in)
      f(params) match
        case e: Error => writeJs(e)
        case e        => writeJs(e.asInstanceOf[req.Out])
    new MCPBuilder(
      requestHandlers.updated(req.method, handler),
      notificationHandlers
    )
  end handleRequest

  def handleNotification(req: MCPNotification & FromClient)(f: req.In => Unit) =
    val handler = (in: ujson.Value) =>
      val params = read[req.In](in)
      f(params)
    new MCPBuilder(
      requestHandlers,
      notificationHandlers.updated(req.method, handler)
    )
  end handleNotification

  def process(is: java.io.InputStream) =

    val reader = new BufferedReader(new InputStreamReader(is))

    def out[J: Writer](j: J) =
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

      System.err.println(s"Outputting ${dropNulls(obj)}")
      System.out.println(write(dropNulls(obj)))
    end out

    var line: String = null
    while { line = reader.readLine(); line != null } do
      val json = read[ujson.Obj](line)
      System.err.println(s"Receiving $json")
      if json.value.contains("id") && json.value.contains("method")
      then // it's a request
        val method = json.value("method").str
        val id = json.value("id")

        val response =
          handleExceptions(id):
            requestHandlers.get(method) match
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

              case Some(value) =>
                value(json) match
                  case err: Error =>
                    Response(id, error = Some(err))

                  case response: ujson.Value =>
                    Response(id, result = Some(writeJs(response)))

        out(response)
      else
        // it's a notification
        val method = json.value("method").str

        notificationHandlers.get(method) match
          case None        => // do nothing
          case Some(value) => value(json)

      end if
    end while
  end process

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
  def create(): MCPBuilder = new MCPBuilder(Map.empty, Map.empty)
