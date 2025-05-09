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

/** The server's response to a resources/read request from the client.
  */
case class ReadResourceResult(
    contents: Seq[ReadResourceResult.Contents],
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None
) derives ReadWriter

object ReadResourceResult:
  val Contents =
    Builder[mcp.TextResourceContents]("TextResourceContents")
      .orElse[mcp.BlobResourceContents]("BlobResourceContents")

  type Contents = Contents.BuilderType
end ReadResourceResult
