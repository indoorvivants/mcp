package sample

import mcp.{*, given}

import java.io.{BufferedReader, InputStreamReader}
import upickle.default.*

case class Request(
    jsonrpc: "2.0",
    id: Id,
    method: String,
    params: Option[ujson.Value] = None
) derives upickle.default.Reader

val Id = Builder[Int]("number").orElse[String]("string")
type Id = Id.BuilderType

given Writer[Id] = summon[Writer[ujson.Value]].comap:
  case i: Int    => ujson.Num(i)
  case i: String => ujson.Str(i)

case class Error(code: Int, message: String, data: Option[ujson.Value] = None)
    derives upickle.default.ReadWriter

case class Response(jsonrpc: "2.0", id: Id, result: Option[ujson.Value] = None)
    derives ReadWriter

case class Notification(
    jsonprc: "2.0",
    method: String,
    params: Option[ujson.Value] = None
) derives ReadWriter

trait MCPRequest(method: String):
  type In 
  type Out

  given ReadWriter[In] = compiletime.deferred
  given ReadWriter[Out] = compiletime.deferred

object initialize extends MCPRequest("initialize"):
  override type In = InitializeRequest
  override type Out = InitializeResult


// object tools:
//   object list extends MCPRequest("initialize"):
//     override type In = 
//     override type Out = InitializeResult



@main def hello =
  val reader = new BufferedReader(new InputStreamReader(System.in))

  val FromClient =
    Builder[Request]("request").orElse[Notification]("notification")
  type FromClient = FromClient.BuilderType

  // Process requests until EOF
  var line: String = null
  while { line = reader.readLine(); line != null } do
    try
      val json = read[ujson.Obj](line)
      if json.value.contains("id") && json.value.contains("method")
      then // it's a request
        val method = json.value("method").str
        val id = read[Id](json.value("id"))

        if method == "initialize" then
          val req = read[InitializeRequest](json)
          val result = writeJs(
            InitializeResult(
              capabilities =
                ServerCapabilities(tools = Some((listChanged = false))),
              protocolVersion = req.params.protocolVersion,
              serverInfo = Implementation("scala-mcp", "0.0.1")
            )
          )

          System.out.println(write(Response("2.0", id, Some(result))))
        end if
      end if
    catch
      case e: Exception =>
        System.err.println(s"Error processing request: ${e.getMessage}")
  end while
end hello
