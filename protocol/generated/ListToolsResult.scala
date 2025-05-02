package mcp

import upickle.default.*

/** The server's response to a tools/list request from the client. */
@upickle.implicits.serializeDefaults(true)
case class ListToolsResult(
  tools: Seq[mcp.Tool],
  /** This result property is reserved by the protocol to allow clients and servers to attach additional metadata to their responses. */
  _meta: Option[ujson.Value] = None,
  /** An opaque token representing the pagination position after the last returned result.
If present, there may be more results available. */
  nextCursor: Option[String],
) derives ReadWriter
