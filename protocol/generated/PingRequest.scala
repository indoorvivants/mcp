package mcp

import mcp.json.*

/** A ping, issued by either the server or the client, to check that the other
  * party is still alive. The receiver must promptly respond, or else may be
  * disconnected.
  */
case class PingRequest(
    method: "ping" = "ping",
    params: Option[PingRequest.Params] = None
) derives ReadWriter

object PingRequest:
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
end PingRequest
