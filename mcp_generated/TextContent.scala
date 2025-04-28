package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Text provided to or from an LLM. */
case class TextContent(
  /** The text content of the message. */
  text: String,
  `type`: "text",
  /** Optional annotations for the client. */
  annotations: Option[mcp.Annotations] = None,
) derives ReadWriter
