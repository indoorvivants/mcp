package mcp

import mcp.json.*

/** Describes a message issued to or received from an LLM API.
  */
case class SamplingMessage(
    content: SamplingMessage.Content,
    role: mcp.Role
) derives ReadWriter

object SamplingMessage:
  val Content =
    Builder[mcp.TextContent]("TextContent")
      .orElse[mcp.ImageContent]("ImageContent")
      .orElse[mcp.AudioContent]("AudioContent")

  type Content = Content.BuilderType
end SamplingMessage
