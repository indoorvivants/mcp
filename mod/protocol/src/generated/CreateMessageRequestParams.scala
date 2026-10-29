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

/** Parameters for a `sampling/createMessage` request.
  */
case class CreateMessageRequestParams(
    /** The requested maximum number of tokens to sample (to prevent runaway
      * completions).
      *
      * The client MAY choose to sample fewer tokens than the requested maximum.
      */
    maxTokens: Int,
    messages: Seq[mcp.SamplingMessage],
    /** See [General fields:
      * `_meta`](/specification/2025-11-25/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[CreateMessageRequestParams._meta] = None,
    /** A request to include context from one or more MCP servers (including the
      * caller), to be attached to the prompt. The client MAY ignore this
      * request.
      *
      * Default is "none". Values "thisServer" and "allServers" are
      * soft-deprecated. Servers SHOULD only use these values if the client
      * declares ClientCapabilities.sampling.context. These values may be
      * removed in future spec releases.
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
    /** If specified, the caller is requesting task-augmented execution for this
      * request. The request will return a CreateTaskResult immediately, and the
      * actual result can be retrieved later via tasks/result.
      *
      * Task augmentation is subject to capability negotiation - receivers MUST
      * declare support for task augmentation of specific request types in their
      * capabilities.
      */
    task: Option[mcp.TaskMetadata] = None,
    temperature: Option[Double] = None,
    /** Controls how the model uses tools. The client MUST return an error if
      * this field is provided but ClientCapabilities.sampling.tools is not
      * declared. Default is `{ mode: "auto" }`.
      */
    toolChoice: Option[mcp.ToolChoice] = None,
    /** Tools that the model may use during generation. The client MUST return
      * an error if this field is provided but ClientCapabilities.sampling.tools
      * is not declared.
      */
    tools: Option[Seq[mcp.Tool]] = None
) derives ReadWriter

object CreateMessageRequestParams:
  case class _meta(
      /** If specified, the caller is requesting out-of-band progress
        * notifications for this request (as represented by
        * notifications/progress). The value of this parameter is an opaque
        * token that will be attached to any subsequent notifications. The
        * receiver is not obligated to provide these notifications.
        */
      progressToken: Option[mcp.ProgressToken] = None
  ) derives ReadWriter
end CreateMessageRequestParams
