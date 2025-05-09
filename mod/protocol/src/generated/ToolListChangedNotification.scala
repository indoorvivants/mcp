package mcp

import mcp.json.*

/** An optional notification from the server to the client, informing it that
  * the list of tools it offers has changed. This may be issued by servers
  * without any previous subscription from the client.
  */
case class ToolListChangedNotification(
    method: "notifications/tools/list_changed" =
      "notifications/tools/list_changed",
    params: Option[ToolListChangedNotification.Params] = None
) derives ReadWriter

object ToolListChangedNotification:
  case class Params(
      /** This parameter name is reserved by MCP to allow clients and servers to
        * attach additional metadata to their notifications.
        */
      _meta: Option[ujson.Obj] = None
  ) derives ReadWriter
end ToolListChangedNotification
