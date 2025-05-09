package mcp

import mcp.json.*

/** An optional notification from the server to the client, informing it that
  * the list of resources it can read from has changed. This may be issued by
  * servers without any previous subscription from the client.
  */
case class ResourceListChangedNotification(
    method: "notifications/resources/list_changed" =
      "notifications/resources/list_changed",
    params: Option[ResourceListChangedNotification.Params] = None
) derives ReadWriter

object ResourceListChangedNotification:
  case class Params(
      /** This parameter name is reserved by MCP to allow clients and servers to
        * attach additional metadata to their notifications.
        */
      _meta: Option[ujson.Obj] = None
  ) derives ReadWriter
end ResourceListChangedNotification
