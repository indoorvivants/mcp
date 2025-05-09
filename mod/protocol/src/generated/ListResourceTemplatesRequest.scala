package mcp

import mcp.json.*

/** Sent from the client to request a list of resource templates the server has.
  */
case class ListResourceTemplatesRequest(
    method: "resources/templates/list" = "resources/templates/list",
    params: Option[ListResourceTemplatesRequest.Params] = None
) derives ReadWriter

object ListResourceTemplatesRequest:
  case class Params(
      /** An opaque token representing the current pagination position. If
        * provided, the server should return results starting after this cursor.
        */
      cursor: Option[String] = None
  ) derives ReadWriter
end ListResourceTemplatesRequest
