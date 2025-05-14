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
  /** A request from the client to the server, to ask for completion options.
    */
  object complete extends MCPRequest("completion/complete"), FromClient:
    /** Params to completion/complete request
      */
    type In = CompleteParams

    /** Response to completion/complete request
      */
    type Out = CompleteResult
  end complete
end completion

/** This request is sent from the client to the server when it first connects,
  * asking it to begin initialization.
  */
object initialize extends MCPRequest("initialize"), FromClient:
  /** Params to initialize request
    */
  type In = InitializeParams

  /** Response to initialize request
    */
  type Out = InitializeResult
end initialize

object notifications:
  /** This notification can be sent by either side to indicate that it is
    * cancelling a previously-issued request.
    *
    * The request SHOULD still be in-flight, but due to communication latency,
    * it is always possible that this notification MAY arrive after the request
    * has already finished.
    *
    * This notification indicates that the result will be unused, so any
    * associated processing SHOULD cease.
    *
    * A client MUST NOT attempt to cancel its `initialize` request.
    */
  object cancelled
      extends MCPNotification("notifications/cancelled"),
        FromClient,
        FromServer:
    /** Params to notifications/cancelled notification
      */
    type In = CancelledParams
  end cancelled

  /** This notification is sent from the client to the server after
    * initialization has finished.
    */
  object initialized
      extends MCPNotification("notifications/initialized"),
        FromClient:
    /** Params to notifications/initialized notification
      */
    type In = InitializedParams
  end initialized

  /** Notification of a log message passed from server to client. If no
    * logging/setLevel request has been sent from the client, the server MAY
    * decide which messages to send automatically.
    */
  object message extends MCPNotification("notifications/message"), FromServer:
    /** Params to notifications/message notification
      */
    type In = LoggingMessageParams
  end message

  /** An out-of-band notification used to inform the receiver of a progress
    * update for a long-running request.
    */
  object progress
      extends MCPNotification("notifications/progress"),
        FromClient,
        FromServer:
    /** Params to notifications/progress notification
      */
    type In = ProgressParams
  end progress

  object prompts:
    /** An optional notification from the server to the client, informing it
      * that the list of prompts it offers has changed. This may be issued by
      * servers without any previous subscription from the client.
      */
    object list_changed
        extends MCPNotification("notifications/prompts/list_changed"),
          FromServer:
      /** Params to notifications/prompts/list_changed notification
        */
      type In = PromptListChangedParams
    end list_changed
  end prompts

  object resources:
    /** An optional notification from the server to the client, informing it
      * that the list of resources it can read from has changed. This may be
      * issued by servers without any previous subscription from the client.
      */
    object list_changed
        extends MCPNotification("notifications/resources/list_changed"),
          FromServer:
      /** Params to notifications/resources/list_changed notification
        */
      type In = ResourceListChangedParams
    end list_changed

    /** A notification from the server to the client, informing it that a
      * resource has changed and may need to be read again. This should only be
      * sent if the client previously sent a resources/subscribe request.
      */
    object updated
        extends MCPNotification("notifications/resources/updated"),
          FromServer:
      /** Params to notifications/resources/updated notification
        */
      type In = ResourceUpdatedParams
    end updated
  end resources

  object roots:
    /** A notification from the client to the server, informing it that the list
      * of roots has changed. This notification should be sent whenever the
      * client adds, removes, or modifies any root. The server should then
      * request an updated list of roots using the ListRootsRequest.
      */
    object list_changed
        extends MCPNotification("notifications/roots/list_changed"),
          FromClient:
      /** Params to notifications/roots/list_changed notification
        */
      type In = RootsListChangedParams
    end list_changed
  end roots

  object tools:
    /** An optional notification from the server to the client, informing it
      * that the list of tools it offers has changed. This may be issued by
      * servers without any previous subscription from the client.
      */
    object list_changed
        extends MCPNotification("notifications/tools/list_changed"),
          FromServer:
      /** Params to notifications/tools/list_changed notification
        */
      type In = ToolListChangedParams
    end list_changed
  end tools
end notifications

/** A ping, issued by either the server or the client, to check that the other
  * party is still alive. The receiver must promptly respond, or else may be
  * disconnected.
  */
object ping extends MCPRequest("ping"), FromClient, FromServer:
  /** Params to ping request
    */
  type In = PingParams

  /** Response to ping request
    */
  type Out = PingResult
end ping

object prompts:
  /** Used by the client to get a prompt provided by the server.
    */
  object get extends MCPRequest("prompts/get"), FromClient:
    /** Params to prompts/get request
      */
    type In = GetPromptParams

    /** Response to prompts/get request
      */
    type Out = GetPromptResult
  end get

  /** Sent from the client to request a list of prompts and prompt templates the
    * server has.
    */
  object list extends MCPRequest("prompts/list"), FromClient:
    /** Params to prompts/list request
      */
    type In = ListPromptsParams

    /** Response to prompts/list request
      */
    type Out = ListPromptsResult
  end list
end prompts

object resources:
  /** Sent from the client to request a list of resources the server has.
    */
  object list extends MCPRequest("resources/list"), FromClient:
    /** Params to resources/list request
      */
    type In = ListResourcesParams

    /** Response to resources/list request
      */
    type Out = ListResourcesResult
  end list

  /** Sent from the client to the server, to read a specific resource URI.
    */
  object read extends MCPRequest("resources/read"), FromClient:
    /** Params to resources/read request
      */
    type In = ReadResourceParams

    /** Response to resources/read request
      */
    type Out = ReadResourceResult
  end read

  /** Sent from the client to request resources/updated notifications from the
    * server whenever a particular resource changes.
    */
  object subscribe extends MCPRequest("resources/subscribe"), FromClient:
    /** Params to resources/subscribe request
      */
    type In = SubscribeParams

    /** Response to resources/subscribe request
      */
    type Out = SubscribeResult
  end subscribe

  object templates:
    /** Sent from the client to request a list of resource templates the server
      * has.
      */
    object list extends MCPRequest("resources/templates/list"), FromClient:
      /** Params to resources/templates/list request
        */
      type In = ListResourceTemplatesParams

      /** Response to resources/templates/list request
        */
      type Out = ListResourceTemplatesResult
    end list
  end templates

  /** Sent from the client to request cancellation of resources/updated
    * notifications from the server. This should follow a previous
    * resources/subscribe request.
    */
  object unsubscribe extends MCPRequest("resources/unsubscribe"), FromClient:
    /** Params to resources/unsubscribe request
      */
    type In = UnsubscribeParams

    /** Response to resources/unsubscribe request
      */
    type Out = UnsubscribeResult
  end unsubscribe
end resources

object roots:
  /** Sent from the server to request a list of root URIs from the client. Roots
    * allow servers to ask for specific directories or files to operate on. A
    * common example for roots is providing a set of repositories or directories
    * a server should operate on.
    *
    * This request is typically used when the server needs to understand the
    * file system structure or access specific locations that the client has
    * permission to read from.
    */
  object list extends MCPRequest("roots/list"), FromServer:
    /** Params to roots/list request
      */
    type In = ListRootsParams

    /** Response to roots/list request
      */
    type Out = ListRootsResult
  end list
end roots

object sampling:
  /** A request from the server to sample an LLM via the client. The client has
    * full discretion over which model to select. The client should also inform
    * the user before beginning sampling, to allow them to inspect the request
    * (human in the loop) and decide whether to approve it.
    */
  object createMessage extends MCPRequest("sampling/createMessage"), FromServer:
    /** Params to sampling/createMessage request
      */
    type In = CreateMessageParams

    /** Response to sampling/createMessage request
      */
    type Out = CreateMessageResult
  end createMessage
end sampling

object tools:
  /** Used by the client to invoke a tool provided by the server.
    */
  object call extends MCPRequest("tools/call"), FromClient:
    /** Params to tools/call request
      */
    type In = CallToolParams

    /** Response to tools/call request
      */
    type Out = CallToolResult
  end call

  /** Sent from the client to request a list of tools the server has.
    */
  object list extends MCPRequest("tools/list"), FromClient:
    /** Params to tools/list request
      */
    type In = ListToolsParams

    /** Response to tools/list request
      */
    type Out = ListToolsResult
  end list
end tools
