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
    /** The name of the tool.
      */
    name: String,
    /** Optional additional tool information.
      */
    annotations: Option[mcp.ToolAnnotations] = None,
    /** A human-readable description of the tool.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available tools. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None
) derives ReadWriter

object Tool:
  case class InputSchema(
      properties: Option[ujson.Obj] = None,
      required: Option[Seq[String]] = None,
      `type`: "object" = "object"
  ) derives ReadWriter
end Tool
