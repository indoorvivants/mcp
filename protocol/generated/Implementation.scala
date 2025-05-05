package mcp

import mcp.json.*

/**
 * Describes the name and version of an MCP implementation.
 */
case class Implementation(
   name: String,
   version: String,
) derives ReadWriter
