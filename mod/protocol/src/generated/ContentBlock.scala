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

/** This type is equivalent to a union type of [[mcp.TextContent]] |
  * [[mcp.ImageContent]] | [[mcp.AudioContent]] | [[mcp.ResourceLink]] |
  * [[mcp.EmbeddedResource]]
  */
type ContentBlock = ContentBlock.BuilderType
val ContentBlock = Builder[mcp.TextContent]("mcp.TextContent")
  .orElse[mcp.ImageContent]("mcp.ImageContent")
  .orElse[mcp.AudioContent]("mcp.AudioContent")
  .orElse[mcp.ResourceLink]("mcp.ResourceLink")
  .orElse[mcp.EmbeddedResource]("mcp.EmbeddedResource")

