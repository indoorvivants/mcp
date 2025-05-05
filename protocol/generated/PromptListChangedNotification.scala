package mcp

import mcp.json.*

/** An optional notification from the server to the client, informing it that
  * the list of prompts it offers has changed. This may be issued by servers
  * without any previous subscription from the client.
  */
case class PromptListChangedNotification(
    method: "notifications/prompts/list_changed" =
      "notifications/prompts/list_changed",
    params: Option[PromptListChangedNotification.Params] = None
) derives ReadWriter

object PromptListChangedNotification:
  case class Params(
      /** This parameter name is reserved by MCP to allow clients and servers to
        * attach additional metadata to their notifications.
        */
      _meta: Option[ujson.Obj] = None
  ) derives ReadWriter
end PromptListChangedNotification
