package mcp

import mcp.json.*

case class TextResourceContents(
    /** The text of the item. This must only be set if the item can actually be
      * represented as text (not binary data).
      */
    text: String,
    /** The URI of this resource.
      */
    uri: String,
    /** The MIME type of this resource, if known.
      */
    mimeType: Option[String] = None
) derives ReadWriter
