/*
 * Copyright 2025 Anton Sviridov
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
    type In = CompleteParams
    type Out = CompleteResult

object initialize extends MCPRequest("initialize"), FromClient:
  type In = InitializeParams
  type Out = InitializeResult

object notifications:
  object cancelled
      extends MCPNotification("notifications/cancelled"),
        FromClient,
        FromServer:
    type In = CancelledParams
  end cancelled

  object initialized
      extends MCPNotification("notifications/initialized"),
        FromClient:
    type In = InitializedParams

  object message extends MCPNotification("notifications/message"), FromServer:
    type In = LoggingMessageParams

  object progress
      extends MCPNotification("notifications/progress"),
        FromClient,
        FromServer:
    type In = ProgressParams
  end progress

  object prompts:
    object list_changed
        extends MCPNotification("notifications/prompts/list_changed"),
          FromServer:
      type In = PromptListChangedParams
  end prompts

  object resources:
    object list_changed
        extends MCPNotification("notifications/resources/list_changed"),
          FromServer:
      type In = ResourceListChangedParams

    object updated
        extends MCPNotification("notifications/resources/updated"),
          FromServer:
      type In = ResourceUpdatedParams
  end resources

  object roots:
    object list_changed
        extends MCPNotification("notifications/roots/list_changed"),
          FromClient:
      type In = RootsListChangedParams
  end roots

  object tools:
    object list_changed
        extends MCPNotification("notifications/tools/list_changed"),
          FromServer:
      type In = ToolListChangedParams
  end tools
end notifications

object ping extends MCPRequest("ping"), FromClient, FromServer:
  type In = PingParams
  type Out = PingResult

object prompts:
  object get extends MCPRequest("prompts/get"), FromClient:
    type In = GetPromptParams
    type Out = GetPromptResult

  object list extends MCPRequest("prompts/list"), FromClient:
    type In = ListPromptsParams
    type Out = ListPromptsResult
end prompts

object resources:
  object list extends MCPRequest("resources/list"), FromClient:
    type In = ListResourcesParams
    type Out = ListResourcesResult

  object read extends MCPRequest("resources/read"), FromClient:
    type In = ReadResourceParams
    type Out = ReadResourceResult

  object subscribe extends MCPRequest("resources/subscribe"), FromClient:
    type In = SubscribeParams
    type Out = SubscribeResult

  object templates:
    object list extends MCPRequest("resources/templates/list"), FromClient:
      type In = ListResourceTemplatesParams
      type Out = ListResourceTemplatesResult

  object unsubscribe extends MCPRequest("resources/unsubscribe"), FromClient:
    type In = UnsubscribeParams
    type Out = UnsubscribeResult
end resources

object roots:
  object list extends MCPRequest("roots/list"), FromServer:
    type In = ListRootsParams
    type Out = ListRootsResult

object sampling:
  object createMessage extends MCPRequest("sampling/createMessage"), FromServer:
    type In = CreateMessageParams
    type Out = CreateMessageResult

object tools:
  object call extends MCPRequest("tools/call"), FromClient:
    type In = CallToolParams
    type Out = CallToolResult

  object list extends MCPRequest("tools/list"), FromClient:
    type In = ListToolsParams
    type Out = ListToolsResult
end tools
