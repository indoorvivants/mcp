package mcp

import mcp.json.*

/**
 * Describes a message returned as part of a prompt.
 * 
 * This is similar to `SamplingMessage`, but also supports the embedding of
 * resources from the MCP server.
 */
case class PromptMessage(
   content: PromptMessage.Content,
   role: mcp.Role,
) derives ReadWriter

object PromptMessage:
   val Content = 
      Builder[mcp.TextContent]("TextContent")
         .orElse[mcp.ImageContent]("ImageContent")
         .orElse[mcp.AudioContent]("AudioContent")
         .orElse[mcp.EmbeddedResource]("EmbeddedResource")
   
   type Content = Content.BuilderType

