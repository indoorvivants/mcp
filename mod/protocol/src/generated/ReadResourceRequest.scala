package mcp

import mcp.json.*

/** Sent from the client to the server, to read a specific resource URI.
  */
case class ReadResourceRequest(
    params: ReadResourceRequest.Params,
    method: "resources/read" = "resources/read"
) derives ReadWriter

object ReadResourceRequest:
  case class Params(
      /** The URI of the resource to read. The URI can use any protocol; it is
        * up to the server how to interpret it.
        */
      uri: String
  ) derives ReadWriter
end ReadResourceRequest
