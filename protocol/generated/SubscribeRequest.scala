package mcp

import mcp.json.*

/** Sent from the client to request resources/updated notifications from the
  * server whenever a particular resource changes.
  */
case class SubscribeRequest(
    params: SubscribeRequest.Params,
    method: "resources/subscribe" = "resources/subscribe"
) derives ReadWriter

object SubscribeRequest:
  case class Params(
      /** The URI of the resource to subscribe to. The URI can use any protocol;
        * it is up to the server how to interpret it.
        */
      uri: String
  ) derives ReadWriter
end SubscribeRequest
