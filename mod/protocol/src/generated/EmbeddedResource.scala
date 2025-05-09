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

/** The contents of a resource, embedded into a prompt or tool call result.
  *
  * It is up to the client how best to render embedded resources for the benefit
  * of the LLM and/or the user.
  */
case class EmbeddedResource(
    resource: EmbeddedResource.Resource,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    `type`: "resource" = "resource"
) derives ReadWriter

object EmbeddedResource:
  val Resource =
    Builder[mcp.TextResourceContents]("TextResourceContents")
      .orElse[mcp.BlobResourceContents]("BlobResourceContents")

  type Resource = Resource.BuilderType
end EmbeddedResource
