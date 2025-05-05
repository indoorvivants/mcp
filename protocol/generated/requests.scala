package mcp

object initialize extends MCPRequest("initialize"):
   type In = InitializeRequest
   type Out = InitializeResult

object ping extends MCPRequest("ping"):
   type In = PingRequest
   type Out = PingResult

object prompts:
   object get extends MCPRequest("prompts/get"):
      type In = GetPromptRequest
      type Out = GetPromptResult
   
   object list extends MCPRequest("prompts/list"):
      type In = ListPromptsRequest
      type Out = ListPromptsResult
   

object resources:
   object list extends MCPRequest("resources/list"):
      type In = ListResourcesRequest
      type Out = ListResourcesResult
   

object tools:
   object call extends MCPRequest("tools/call"):
      type In = CallToolRequest
      type Out = CallToolResult
   
   object list extends MCPRequest("tools/list"):
      type In = ListToolsRequest
      type Out = ListToolsResult
   

