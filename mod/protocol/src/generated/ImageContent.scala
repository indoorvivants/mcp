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

/** An image provided to or from an LLM.
  */
case class ImageContent(
    /** The base64-encoded image data.
      */
    data: String,
    /** The MIME type of the image. Different providers may support different
      * image types.
      */
    mimeType: String,
    /** Optional annotations for the client.
      */
    annotations: Option[mcp.Annotations] = None,
    `type`: "image" = "image"
) derives ReadWriter
