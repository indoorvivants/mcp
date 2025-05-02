package mcp

import upickle.default.*

/** The contents of a resource, embedded into a prompt or tool call result.

It is up to the client how best to render embedded resources for the benefit
of the LLM and/or the user. */
@upickle.implicits.serializeDefaults(true)
case class EmbeddedResource(
  resource: Any /*List(Ref(#/definitions/TextResourceContents,None), Ref(#/definitions/BlobResourceContents,None))*/,
  `type`: "resource" = "resource",
  /** Optional annotations for the client. */
  annotations: Option[mcp.Annotations] = None,
) derives ReadWriter

object EmbeddedResource:
  val Resource = 
    Builder[mcp.TextResourceContents]("TextResourceContents")
      .orElse[mcp.BlobResourceContents]("BlobResourceContents")
  
  type Resource = Resource.BuilderType

