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
    /** A human-readable name for the type of resource this template refers to.
      *
      * This can be used by clients to populate UI elements.
      */
    name: String,
    /** A URI template (according to RFC 6570) that can be used to construct
      * resource URIs.
      */
    uriTemplate: String,
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
    mimeType: Option[String] = None
) derives ReadWriter
