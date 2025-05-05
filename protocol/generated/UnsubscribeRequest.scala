package mcp

import mcp.json.*

/** Sent from the client to request cancellation of resources/updated
  * notifications from the server. This should follow a previous
  * resources/subscribe request.
  */
case class UnsubscribeRequest(
    params: UnsubscribeRequest.Params,
    method: "resources/unsubscribe" = "resources/unsubscribe"
) derives ReadWriter

object UnsubscribeRequest:
  case class Params(
      /** The URI of the resource to unsubscribe from.
        */
      uri: String
  ) derives ReadWriter
end UnsubscribeRequest
