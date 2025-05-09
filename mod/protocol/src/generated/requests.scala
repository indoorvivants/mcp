/*
 * Copyright 2020 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcp

object completion:
  object complete extends MCPRequest("completion/complete"), FromClient:
    type In = CompleteRequest
    type Out = CompleteResult

object initialize extends MCPRequest("initialize"), FromClient:
  type In = InitializeRequest
  type Out = InitializeResult

object notifications:
  object cancelled
      extends MCPNotification("notifications/cancelled"),
        FromClient,
        FromServer:
    type In = CancelledNotification
  end cancelled

  object initialized
      extends MCPNotification("notifications/initialized"),
        FromClient:
    type In = InitializedNotification

  object message extends MCPNotification("notifications/message"), FromServer:
    type In = LoggingMessageNotification

  object progress
      extends MCPNotification("notifications/progress"),
        FromClient,
        FromServer:
    type In = ProgressNotification
  end progress

  object prompts:
    object list_changed
        extends MCPNotification("notifications/prompts/list_changed"),
          FromServer:
      type In = PromptListChangedNotification
  end prompts

  object resources:
    object list_changed
        extends MCPNotification("notifications/resources/list_changed"),
          FromServer:
      type In = ResourceListChangedNotification

    object updated
        extends MCPNotification("notifications/resources/updated"),
          FromServer:
      type In = ResourceUpdatedNotification
  end resources

  object roots:
    object list_changed
        extends MCPNotification("notifications/roots/list_changed"),
          FromClient:
      type In = RootsListChangedNotification
  end roots

  object tools:
    object list_changed
        extends MCPNotification("notifications/tools/list_changed"),
          FromServer:
      type In = ToolListChangedNotification
  end tools
end notifications

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
end prompts

object resources:
  object list extends MCPRequest("resources/list"), FromClient:
    type In = ListResourcesRequest
    type Out = ListResourcesResult

  object read extends MCPRequest("resources/read"), FromClient:
    type In = ReadResourceRequest
    type Out = ReadResourceResult

  object subscribe extends MCPRequest("resources/subscribe"), FromClient:
    type In = SubscribeRequest
    type Out = SubscribeResult

  object templates:
    object list extends MCPRequest("resources/templates/list"), FromClient:
      type In = ListResourceTemplatesRequest
      type Out = ListResourceTemplatesResult

  object unsubscribe extends MCPRequest("resources/unsubscribe"), FromClient:
    type In = UnsubscribeRequest
    type Out = UnsubscribeResult
end resources

object roots:
  object list extends MCPRequest("roots/list"), FromServer:
    type In = ListRootsRequest
    type Out = ListRootsResult

object sampling:
  object createMessage extends MCPRequest("sampling/createMessage"), FromServer:
    type In = CreateMessageRequest
    type Out = CreateMessageResult

object tools:
  object call extends MCPRequest("tools/call"), FromClient:
    type In = CallToolRequest
    type Out = CallToolResult

  object list extends MCPRequest("tools/list"), FromClient:
    type In = ListToolsRequest
    type Out = ListToolsResult
end tools
