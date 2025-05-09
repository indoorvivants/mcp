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

/** Used by the client to get a prompt provided by the server.
  */
case class GetPromptRequest(
    params: GetPromptRequest.Params,
    method: "prompts/get" = "prompts/get"
) derives ReadWriter

object GetPromptRequest:
  case class Params(
      /** The name of the prompt or prompt template.
        */
      name: String,
      /** Arguments to use for templating the prompt.
        */
      arguments: Option[ujson.Obj] = None
  ) derives ReadWriter
end GetPromptRequest
