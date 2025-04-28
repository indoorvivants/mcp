package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Describes the name and version of an MCP implementation. */
case class Implementation(
  name: String,
  version: String,
) derives ReadWriter
