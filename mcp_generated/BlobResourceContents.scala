package mcp

import upickle.default.*

case class BlobResourceContents(
  /** A base64-encoded string representing the binary data of the item. */
  blob: String,
  /** The URI of this resource. */
  uri: String,
  /** The MIME type of this resource, if known. */
  mimeType: Option[String] = None,
) derives ReadWriter
