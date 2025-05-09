package mcp

import mcp.json.*

/** Describes an argument that a prompt can accept.
  */
case class PromptArgument(
    /** The name of the argument.
      */
    name: String,
    /** A human-readable description of the argument.
      */
    description: Option[String] = None,
    required: Option[Boolean] = None
) derives ReadWriter
