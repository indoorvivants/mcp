package mcp

import mcp.json.*

/** Identifies a prompt.
  */
case class PromptReference(
    /** The name of the prompt or prompt template
      */
    name: String,
    `type`: "ref/prompt" = "ref/prompt"
) derives ReadWriter
