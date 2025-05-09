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

/** A request from the server to sample an LLM via the client. The client has
  * full discretion over which model to select. The client should also inform
  * the user before beginning sampling, to allow them to inspect the request
  * (human in the loop) and decide whether to approve it.
  */
case class CreateMessageRequest(
    params: CreateMessageRequest.Params,
    method: "sampling/createMessage" = "sampling/createMessage"
) derives ReadWriter

object CreateMessageRequest:
  case class Params(
      /** The maximum number of tokens to sample, as requested by the server.
        * The client MAY choose to sample fewer tokens than requested.
        */
      maxTokens: Int,
      messages: Seq[mcp.SamplingMessage],
      /** A request to include context from one or more MCP servers (including
        * the caller), to be attached to the prompt. The client MAY ignore this
        * request.
        */
      includeContext: Option[String] = None,
      /** Optional metadata to pass through to the LLM provider. The format of
        * this metadata is provider-specific.
        */
      metadata: Option[ujson.Obj] = None,
      /** The server's preferences for which model to select. The client MAY
        * ignore these preferences.
        */
      modelPreferences: Option[mcp.ModelPreferences] = None,
      stopSequences: Option[Seq[String]] = None,
      /** An optional system prompt the server wants to use for sampling. The
        * client MAY modify or omit this prompt.
        */
      systemPrompt: Option[String] = None,
      temperature: Option[Double] = None
  ) derives ReadWriter
end CreateMessageRequest
