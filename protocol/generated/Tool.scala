package mcp

import upickle.default.*

/** Definition for a tool the client can call. */
@upickle.implicits.serializeDefaults(true)
case class Tool(
  /** A JSON Schema object defining the expected parameters for the tool. */
  inputSchema: Tool.InputSchema,
  /** The name of the tool. */
  name: String,
  /** Optional additional tool information. */
  annotations: Option[mcp.ToolAnnotations] = None,
  /** A human-readable description of the tool.

This can be used by clients to improve the LLM's understanding of available tools. It can be thought of like a "hint" to the model. */
  description: Option[String],
) derives ReadWriter

object Tool:
  @upickle.implicits.serializeDefaults(true)
  case class InputSchema(
    properties: Option[ujson.Value] = None,
    required: Option[Seq[String]] = None,
    `type`: "object" = "object",
  ) derives ReadWriter


