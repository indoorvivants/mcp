package mcp

import mcp.json.*

/**
 * The contents of a resource, embedded into a prompt or tool call result.
 * 
 * It is up to the client how best to render embedded resources for the benefit
 * of the LLM and/or the user.
 */
case class EmbeddedResource(
   resource: EmbeddedResource.Resource,
   /**
    * Optional annotations for the client.
    */
   annotations: Option[mcp.Annotations] = None,
   `type`: "resource" = "resource",
) derives ReadWriter

object EmbeddedResource:
   val Resource = 
      Builder[mcp.TextResourceContents]("TextResourceContents")
         .orElse[mcp.BlobResourceContents]("BlobResourceContents")
   
   type Resource = Resource.BuilderType

