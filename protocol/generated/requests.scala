package mcp

object initialize extends MCPRequest("initialize"):
   type In = InitializeRequest
   type Out = InitializeResult

object ping extends MCPRequest("ping"):
   type In = PingRequest
   type Out = PingResult

object tools:
   object call extends MCPRequest("tools/call"):
      type In = CallToolRequest
      type Out = CallToolResult
   
   object list extends MCPRequest("tools/list"):
      type In = ListToolsRequest
      type Out = ListToolsResult
   

