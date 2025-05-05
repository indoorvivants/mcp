package mcp

import mcp.json.*

/** The server's response to a resources/list request from the client.
  */
case class ListResourcesResult(
    resources: Seq[mcp.Resource],
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None,
    /** An opaque token representing the pagination position after the last
      * returned result. If present, there may be more results available.
      */
    nextCursor: Option[String] = None
) derives ReadWriter
