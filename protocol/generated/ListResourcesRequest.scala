package mcp

import mcp.json.*

/**
 * Sent from the client to request a list of resources the server has.
 */
case class ListResourcesRequest(
   method: "resources/list" = "resources/list",
   params: Option[ListResourcesRequest.Params] = None,
) derives ReadWriter

object ListResourcesRequest:
   case class Params(
      /**
       * An opaque token representing the current pagination position.
       * If provided, the server should return results starting after this cursor.
       */
      cursor: Option[String] = None,
   ) derives ReadWriter


