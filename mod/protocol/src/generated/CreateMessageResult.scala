package mcp

import mcp.json.*

/** The client's response to a sampling/create_message request from the server.
  * The client should inform the user before returning the sampled message, to
  * allow them to inspect the response (human in the loop) and decide whether to
  * allow the server to see it.
  */
case class CreateMessageResult(
    content: CreateMessageResult.Content,
    /** The name of the model that generated the message.
      */
    model: String,
    role: mcp.Role,
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None,
    /** The reason why sampling stopped, if known.
      */
    stopReason: Option[String] = None
) derives ReadWriter

object CreateMessageResult:
  val Content =
    Builder[mcp.TextContent]("TextContent")
      .orElse[mcp.ImageContent]("ImageContent")
      .orElse[mcp.AudioContent]("AudioContent")

  type Content = Content.BuilderType
end CreateMessageResult
