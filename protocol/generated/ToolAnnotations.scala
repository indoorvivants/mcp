package mcp

import mcp.json.*

/**
 * Additional properties describing a Tool to clients.
 * 
 * NOTE: all properties in ToolAnnotations are **hints**.
 * They are not guaranteed to provide a faithful description of
 * tool behavior (including descriptive properties like `title`).
 * 
 * Clients should never make tool use decisions based on ToolAnnotations
 * received from untrusted servers.
 */
case class ToolAnnotations(
   destructiveHint: Option[Boolean] = None,
   idempotentHint: Option[Boolean] = None,
   openWorldHint: Option[Boolean] = None,
   readOnlyHint: Option[Boolean] = None,
   /**
    * A human-readable title for the tool.
    */
   title: Option[String] = None,
) derives ReadWriter
