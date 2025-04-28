package mcp

import upickle.default.*

/** After receiving an initialize request from the client, the server sends this response. */
case class InitializeResult(
  capabilities: mcp.ServerCapabilities,
  /** The version of the Model Context Protocol that the server wants to use. This may not match the version that the client requested. If the client cannot support this version, it MUST disconnect. */
  protocolVersion: String,
  serverInfo: mcp.Implementation,
  /** This result property is reserved by the protocol to allow clients and servers to attach additional metadata to their responses. */
  _meta: Option[ujson.Value] = None,
  /** Instructions describing how to use the server and its features.

This can be used by clients to improve the LLM's understanding of available tools, resources, etc. It can be thought of like a "hint" to the model. For example, this information MAY be added to the system prompt. */
  instructions: Option[String] = None,
) derives ReadWriter
