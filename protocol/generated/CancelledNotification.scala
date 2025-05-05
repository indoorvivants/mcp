package mcp

import mcp.json.*

/**
 * This notification can be sent by either side to indicate that it is cancelling a previously-issued request.
 * 
 * The request SHOULD still be in-flight, but due to communication latency, it is always possible that this notification MAY arrive after the request has already finished.
 * 
 * This notification indicates that the result will be unused, so any associated processing SHOULD cease.
 * 
 * A client MUST NOT attempt to cancel its `initialize` request.
 */
case class CancelledNotification(
   params: CancelledNotification.Params,
   method: "notifications/cancelled" = "notifications/cancelled",
) derives ReadWriter

object CancelledNotification:
   case class Params(
      /**
       * The ID of the request to cancel.
       * 
       * This MUST correspond to the ID of a request previously issued in the same direction.
       */
      requestId: mcp.RequestId,
      /**
       * An optional string describing the reason for the cancellation. This MAY be logged or presented to the user.
       */
      reason: Option[String] = None,
   ) derives ReadWriter


