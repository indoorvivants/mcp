package mcp

import mcp.json.*

/** A reference to a resource or resource template definition.
  */
case class ResourceReference(
    /** The URI or URI template of the resource.
      */
    uri: String,
    `type`: "ref/resource" = "ref/resource"
) derives ReadWriter
