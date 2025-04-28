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
      ListToolsResult(Seq.empty)
    .process(System.in)
end hello
