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

/** A resource that the server is capable of reading, included in a prompt or
  * tool call result.
  *
  * Note: resource links returned by tools are not guaranteed to appear in the
  * results of `resources/list` requests.
  */
case class ResourceLink(
    /** Intended for programmatic or logical use, but used as a display name in
      * past specs or fallback (if title isn't present).
      */
    name: String,
    /** The URI of this resource.
      */
    uri: String,
    /** See [General fields:
      * `_meta`](/specification/2025-06-18/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    /** A description of what this resource represents.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available resources. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None,
    /** The MIME type of this resource, if known.
      */
    mimeType: Option[String] = None,
    /** The size of the raw resource content, in bytes (i.e., before base64
      * encoding or any tokenization), if known.
      *
      * This can be used by Hosts to display file sizes and estimate context
      * window usage.
      */
    size: Option[Int] = None,
    /** Intended for UI and end-user contexts — optimized to be human-readable
      * and easily understood, even by those unfamiliar with domain-specific
      * terminology.
      *
      * If not provided, the name should be used for display (except for Tool,
      * where `annotations.title` should be given precedence over using `name`,
      * if present).
      */
    title: Option[String] = None,
    `type`: "resource_link" = "resource_link"
) derives ReadWriter
