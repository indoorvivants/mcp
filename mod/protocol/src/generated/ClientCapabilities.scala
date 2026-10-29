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

/** Capabilities a client may support. Known capabilities are defined here, in
  * this schema, but this is not a closed set: any client can define its own,
  * additional capabilities.
  */
case class ClientCapabilities(
    /** Present if the client supports elicitation from the server.
      */
    elicitation: Option[ClientCapabilities.Elicitation] = None,
    /** Experimental, non-standard capabilities that the client supports.
      */
    experimental: Option[ujson.Obj] = None,
    /** Present if the client supports listing roots.
      */
    roots: Option[ClientCapabilities.Roots] = None,
    /** Present if the client supports sampling from an LLM.
      */
    sampling: Option[ClientCapabilities.Sampling] = None,
    /** Present if the client supports task-augmented requests.
      */
    tasks: Option[ClientCapabilities.Tasks] = None
) derives ReadWriter

object ClientCapabilities:
  case class Elicitation(
      form: Option[ujson.Obj] = None,
      url: Option[ujson.Obj] = None
  ) derives ReadWriter
  case class Roots(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
  case class Sampling(
      /** Whether the client supports context inclusion via includeContext
        * parameter. If not declared, servers SHOULD only use
        * `includeContext: "none"` (or omit it).
        */
      context: Option[ujson.Obj] = None,
      /** Whether the client supports tool use via tools and toolChoice
        * parameters.
        */
      tools: Option[ujson.Obj] = None
  ) derives ReadWriter
  case class Tasks(
      /** Whether this client supports tasks/cancel.
        */
      cancel: Option[ujson.Obj] = None,
      /** Whether this client supports tasks/list.
        */
      list: Option[ujson.Obj] = None,
      /** Specifies which request types can be augmented with tasks.
        */
      requests: Option[Tasks.Requests] = None
  ) derives ReadWriter

  object Tasks:
    case class Requests(
        /** Task support for elicitation-related requests.
          */
        elicitation: Option[Requests.Elicitation] = None,
        /** Task support for sampling-related requests.
          */
        sampling: Option[Requests.Sampling] = None
    ) derives ReadWriter

    object Requests:
      case class Elicitation(
          /** Whether the client supports task-augmented elicitation/create
            * requests.
            */
          create: Option[ujson.Obj] = None
      ) derives ReadWriter
      case class Sampling(
          /** Whether the client supports task-augmented sampling/createMessage
            * requests.
            */
          createMessage: Option[ujson.Obj] = None
      ) derives ReadWriter
    end Requests
  end Tasks
end ClientCapabilities
