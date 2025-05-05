package mcp

import mcp.json.*

/** A template description for resources available on the server.
  */
case class ResourceTemplate(
    /** A human-readable name for the type of resource this template refers to.
      *
      * This can be used by clients to populate UI elements.
      */
    name: String,
    /** A URI template (according to RFC 6570) that can be used to construct
      * resource URIs.
      */
    uriTemplate: String,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    /** A description of what this template is for.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available resources. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None,
    /** The MIME type for all resources that match this template. This should
      * only be included if all resources matching this template have the same
      * type.
      */
    mimeType: Option[String] = None
) derives ReadWriter
