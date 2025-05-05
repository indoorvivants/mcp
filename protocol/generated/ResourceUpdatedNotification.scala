package mcp

import mcp.json.*

/** A notification from the server to the client, informing it that a resource
  * has changed and may need to be read again. This should only be sent if the
  * client previously sent a resources/subscribe request.
  */
case class ResourceUpdatedNotification(
    params: ResourceUpdatedNotification.Params,
    method: "notifications/resources/updated" =
      "notifications/resources/updated"
) derives ReadWriter

object ResourceUpdatedNotification:
  case class Params(
      /** The URI of the resource that has been updated. This might be a
        * sub-resource of the one that the client actually subscribed to.
        */
      uri: String
  ) derives ReadWriter
end ResourceUpdatedNotification
