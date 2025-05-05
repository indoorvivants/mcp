package mcp

import mcp.json.*

/**
 * Text provided to or from an LLM.
 */
case class TextContent(
   /**
    * The text content of the message.
    */
   text: String,
   /**
    * Optional annotations for the client.
    */
   annotations: Option[mcp.Annotations] = None,
   `type`: "text" = "text",
) derives ReadWriter
