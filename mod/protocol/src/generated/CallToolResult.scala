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

/** The server's response to a tool call.
  */
case class CallToolResult(
    /** A list of content objects that represent the unstructured result of the
      * tool call.
      */
    content: Seq[mcp.ContentBlock],
    /** See [General fields:
      * `_meta`](/specification/2025-06-18/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    isError: Option[Boolean] = None,
    /** An optional JSON object that represents the structured result of the
      * tool call.
      */
    structuredContent: Option[ujson.Obj] = None
) derives ReadWriter
