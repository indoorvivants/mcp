/*
 * Copyright 2020 Anton Sviridov
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

/** The client's response to a sampling/create_message request from the server.
  * The client should inform the user before returning the sampled message, to
  * allow them to inspect the response (human in the loop) and decide whether to
  * allow the server to see it.
  */
case class CreateMessageResult(
    content: CreateMessageResult.Content,
    /** The name of the model that generated the message.
      */
    model: String,
    role: mcp.Role,
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None,
    /** The reason why sampling stopped, if known.
      */
    stopReason: Option[String] = None
) derives ReadWriter

object CreateMessageResult:
  val Content =
    Builder[mcp.TextContent]("TextContent")
      .orElse[mcp.ImageContent]("ImageContent")
      .orElse[mcp.AudioContent]("AudioContent")

  type Content = Content.BuilderType
end CreateMessageResult
