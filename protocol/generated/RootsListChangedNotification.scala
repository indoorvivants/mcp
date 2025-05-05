package mcp

import mcp.json.*

/** A notification from the client to the server, informing it that the list of
  * roots has changed. This notification should be sent whenever the client
  * adds, removes, or modifies any root. The server should then request an
  * updated list of roots using the ListRootsRequest.
  */
case class RootsListChangedNotification(
    method: "notifications/roots/list_changed" =
      "notifications/roots/list_changed",
    params: Option[RootsListChangedNotification.Params] = None
) derives ReadWriter

object RootsListChangedNotification:
  case class Params(
      /** This parameter name is reserved by MCP to allow clients and servers to
        * attach additional metadata to their notifications.
        */
      _meta: Option[ujson.Obj] = None
  ) derives ReadWriter
end RootsListChangedNotification
