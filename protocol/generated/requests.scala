package mcp

object initialize extends MCPRequest("initialize"), FromClient:
   type In = InitializeRequest
   type Out = InitializeResult

object notifications:
   object cancelled extends MCPNotification("notifications/cancelled"), FromClient, FromServer:
      type In = CancelledNotification
   
   object initialized extends MCPNotification("notifications/initialized"), FromClient:
      type In = InitializedNotification
   

object ping extends MCPRequest("ping"), FromClient, FromServer:
   type In = PingRequest
   type Out = PingResult

object prompts:
   object get extends MCPRequest("prompts/get"), FromClient:
      type In = GetPromptRequest
      type Out = GetPromptResult
   
   object list extends MCPRequest("prompts/list"), FromClient:
      type In = ListPromptsRequest
      type Out = ListPromptsResult
   

object resources:
   object list extends MCPRequest("resources/list"), FromClient:
      type In = ListResourcesRequest
      type Out = ListResourcesResult
   

object tools:
   object call extends MCPRequest("tools/call"), FromClient:
      type In = CallToolRequest
      type Out = CallToolResult
   
   object list extends MCPRequest("tools/list"), FromClient:
      type In = ListToolsRequest
      type Out = ListToolsResult
   

