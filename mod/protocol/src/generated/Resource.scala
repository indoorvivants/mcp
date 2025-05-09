package mcp

import mcp.json.*

/** A known resource that the server is capable of reading.
  */
case class Resource(
    /** A human-readable name for this resource.
      *
      * This can be used by clients to populate UI elements.
      */
    name: String,
    /** The URI of this resource.
      */
    uri: String,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    /** A description of what this resource represents.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available resources. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None,
    /** The MIME type of this resource, if known.
      */
    mimeType: Option[String] = None,
    size: Option[Int] = None
) derives ReadWriter
