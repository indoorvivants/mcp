package mcp

import mcp.json.*

/**
 * A prompt or prompt template that the server offers.
 */
case class Prompt(
   /**
    * The name of the prompt or prompt template.
    */
   name: String,
   /**
    * A list of arguments to use for templating the prompt.
    */
   arguments: Option[Seq[mcp.PromptArgument]],
   /**
    * An optional description of what this prompt provides
    */
   description: Option[String] = None,
) derives ReadWriter
