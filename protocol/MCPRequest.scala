package mcp

import upickle.default.*

trait MCPRequest(val method: String):
  type In
  type Out

  given ReadWriter[In] = compiletime.deferred
  given ReadWriter[Out] = compiletime.deferred
end MCPRequest

object initialize extends MCPRequest("initialize"):
  override type In = InitializeRequest
  override type Out = InitializeResult

object tools:
  object list extends MCPRequest("tools/list"):
    override type In = ListToolsRequest
    override type Out = ListToolsResult

  object call extends MCPRequest("tools/call"):
    override type In = CallToolRequest
    override type Out = CallToolResult
end tools
