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

/** A request from the client to the server, to ask for completion options.
  */
case class CompleteRequest(
    params: CompleteRequest.Params,
    method: "completion/complete" = "completion/complete"
) derives ReadWriter

object CompleteRequest:
  case class Params(
      /** The argument's information
        */
      argument: Params.Argument,
      ref: Params.Ref
  ) derives ReadWriter

  object Params:
    case class Argument(
        /** The name of the argument
          */
        name: String,
        /** The value of the argument to use for completion matching.
          */
        value: String
    ) derives ReadWriter

    val Ref =
      Builder[mcp.PromptReference]("PromptReference")
        .orElse[mcp.ResourceReference]("ResourceReference")

    type Ref = Ref.BuilderType
  end Params
end CompleteRequest
