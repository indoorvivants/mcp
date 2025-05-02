package mcp

import upickle.default.*

/** Describes the name and version of an MCP implementation. */
@upickle.implicits.serializeDefaults(true)
case class Implementation(
  name: String,
  version: String,
) derives ReadWriter
