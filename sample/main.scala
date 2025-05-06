//> using dep com.softwaremill.sttp.client4::core::4.0.3
//> using dep com.softwaremill.sttp.client4::upickle::4.0.3
//> using scala 3.7.0
//> using option -Wunused:all

import mcp.*
import upickle.default.*
import sttp.client4.*

val backend = DefaultSyncBackend()

def weather(city: String) =
  basicRequest
    .get(uri"https://wttr.in/$city?format=4")
    .send(backend)
    .body
    .right
    .get

@main def getWeatherScalaMCP =
  val mcp = MCPBuilder
    .create()
    .verbose
    .handle(ping)(_ => PingResult())
    .handle(initialize): req =>
      InitializeResult(
        capabilities =
          ServerCapabilities(tools = Some(ServerCapabilities.Tools())),
        protocolVersion = req.params.protocolVersion,
        serverInfo = Implementation("get-weather-scala-mcp", "0.0.1")
      )
    .handle(notifications.initialized): n =>
      // Send notifications to the client
      communicate.notification(
        notifications.tools.list_changed,
        ToolListChangedNotification()
      )
      // Send requests and receive responses from the client
      System.err.println(
        communicate.request(
          sampling.createMessage,
          CreateMessageRequest(
            params = CreateMessageRequest.Params(
              maxTokens = 500,
              messages = Seq(
                SamplingMessage(
                  TextContent("what is the meaning of life?"),
                  role = Role.user
                )
              )
            )
          )
        )
      )
    .handle(tools.list): _ =>
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
    .handle(tools.call): req =>
      val location = req.params.arguments.get.obj("location").str
      CallToolResult(content =
        Seq(
          TextContent(text = weather(location), `type` = "text")
        )
      )
    .run()
end getWeatherScalaMCP
