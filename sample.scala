package sample

import mcp.*
import upickle.default.*

@main def hello =
  val mcp = MCPBuilder
    .create()
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
            name = "Get weather (but in Scala)",
            inputSchema = Tool.InputSchema(
              Some(
                ujson.Obj(
                  "location" -> ujson.Obj("type" -> ujson.Str("string"))
                )
              ),
              required = Some(Seq("location")),
              `type` = "object"
            )
          )
        )
      )
    .process(System.in)
end hello
