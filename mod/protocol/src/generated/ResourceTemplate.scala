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

/** A template description for resources available on the server.
  */
case class ResourceTemplate(
    /** Intended for programmatic or logical use, but used as a display name in
      * past specs or fallback (if title isn't present).
      */
    name: String,
    /** A URI template (according to RFC 6570) that can be used to construct
      * resource URIs.
      */
    uriTemplate: String,
    /** See [General fields:
      * `_meta`](/specification/2025-06-18/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    /** A description of what this template is for.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available resources. It can be thought of like a "hint" to the model.
      */
    description: Option[String] = None,
    /** The MIME type for all resources that match this template. This should
      * only be included if all resources matching this template have the same
      * type.
      */
    mimeType: Option[String] = None,
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
