//> using dep com.softwaremill.sttp.client4::core::4.0.3
//> using dep com.softwaremill.sttp.client4::upickle::4.0.3
//> using scala 3.7.0-RC4

import mcp.*
import upickle.default.*

import sttp.client4.*
import sttp.client4.upicklejson.default.*

val backend = DefaultSyncBackend()

def weather(city: String) =
  basicRequest
    .get(uri"https://wttr.in/$city?format=4")
    .send(backend)
    .body
    .right
    .get

@main def hello =
  val mcp = MCPBuilder
    .create()
    .handlePings()
    .handleRequest(initialize): req =>
      InitializeResult(
        capabilities =
          ServerCapabilities(tools = Some(ServerCapabilities.Tools())),
        protocolVersion = req.params.protocolVersion,
        serverInfo = Implementation("scala-mcp", "0.0.1")
      )
    .handleRequest(tools.list): req =>
      ListToolsResult(
        Seq(
          Tool(
            name = "get_weather",
            description =
              Some("Get weather in concise form in a given location"),
            inputSchema = Tool.InputSchema(
              Some(
                ujson.Obj(
                  "location" -> ujson.Obj("type" -> ujson.Str("string"))
                )
              ),
              required = Some(Seq("location"))
            )
          )
        )
      )
    .handleRequest(tools.call): req =>
      val location = req.params.arguments.get.obj("location").str
      CallToolResult(content =
        Seq(
          TextContent(text = weather(location), `type` = "text")
        )
      )
    .process(System.in)
end hello
