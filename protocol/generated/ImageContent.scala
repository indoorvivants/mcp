package mcp

import mcp.json.*

/** An image provided to or from an LLM.
  */
case class ImageContent(
    /** The base64-encoded image data.
      */
    data: String,
    /** The MIME type of the image. Different providers may support different
      * image types.
      */
    mimeType: String,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    `type`: "image" = "image"
) derives ReadWriter
