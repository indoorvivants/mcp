package mcp

import upickle.default.*

/** The server's response to a tool call.

Any errors that originate from the tool SHOULD be reported inside the result
object, with `isError` set to true, _not_ as an MCP protocol-level error
response. Otherwise, the LLM would not be able to see that an error occurred
and self-correct.

However, any errors in _finding_ the tool, an error indicating that the
server does not support tool calls, or any other exceptional conditions,
should be reported as an MCP error response. */
@upickle.implicits.serializeDefaults(true)
case class CallToolResult(
  content: Seq[CallToolResult.Content],
  /** This result property is reserved by the protocol to allow clients and servers to attach additional metadata to their responses. */
  _meta: Option[ujson.Value] = None,
  isError: Option[Boolean] = None,
) derives ReadWriter

object CallToolResult:
  val Content = 
    Builder[mcp.TextContent]("TextContent")
      .orElse[mcp.ImageContent]("ImageContent")
      .orElse[mcp.AudioContent]("AudioContent")
      .orElse[mcp.EmbeddedResource]("EmbeddedResource")
  
  type Content = Content.BuilderType

