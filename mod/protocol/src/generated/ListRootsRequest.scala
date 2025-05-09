package mcp

import mcp.json.*

/** Sent from the server to request a list of root URIs from the client. Roots
  * allow servers to ask for specific directories or files to operate on. A
  * common example for roots is providing a set of repositories or directories a
  * server should operate on.
  *
  * This request is typically used when the server needs to understand the file
  * system structure or access specific locations that the client has permission
  * to read from.
  */
case class ListRootsRequest(
    method: "roots/list" = "roots/list",
    params: Option[ListRootsRequest.Params] = None
) derives ReadWriter

object ListRootsRequest:
  case class Params(
      _meta: Option[Params._meta] = None
  ) derives ReadWriter

  object Params:
    case class _meta(
        /** If specified, the caller is requesting out-of-band progress
          * notifications for this request (as represented by
          * notifications/progress). The value of this parameter is an opaque
          * token that will be attached to any subsequent notifications. The
          * receiver is not obligated to provide these notifications.
          */
        progressToken: Option[mcp.ProgressToken] = None
    ) derives ReadWriter
  end Params
end ListRootsRequest
