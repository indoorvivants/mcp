/*
 * Copyright 2025 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcp

import mcp.json.*

/** Definition for a tool the client can call.
  */
case class Tool(
    /** A JSON Schema object defining the expected parameters for the tool.
      */
    inputSchema: Tool.InputSchema,
    /** Intended for programmatic or logical use, but used as a display name in
      * past specs or fallback (if title isn't present).
      */
    name: String,
    /** See [General fields:
      * `_meta`](/specification/2025-06-18/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** Optional additional tool information.
      *
      * Display name precedence order is: title, annotations.title, then name.
      */
    annotations: Option[mcp.ToolAnnotations] = None,
    /** A human-readable description of the tool.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available tools. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None,
    /** An optional JSON Schema object defining the structure of the tool's
      * output returned in the structuredContent field of a CallToolResult.
      */
    outputSchema: Option[Tool.OutputSchema] = None,
    /** Intended for UI and end-user contexts — optimized to be human-readable
      * and easily understood, even by those unfamiliar with domain-specific
      * terminology.
      *
      * If not provided, the name should be used for display (except for Tool,
      * where `annotations.title` should be given precedence over using `name`,
      * if present).
      */
    title: Option[String] = None
) derives ReadWriter

object Tool:
  case class InputSchema(
      properties: Option[ujson.Obj] = None,
      required: Option[Seq[String]] = None,
      `type`: "object" = "object"
  ) derives ReadWriter
  case class OutputSchema(
      properties: Option[ujson.Obj] = None,
      required: Option[Seq[String]] = None,
      `type`: "object" = "object"
  ) derives ReadWriter
end Tool
