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

/** Hints to use for model selection.
  *
  * Keys not declared here are currently left unspecified by the spec and are up
  * to the client to interpret.
  */
case class ModelHint(
    /** A hint for a model name.
      *
      * The client SHOULD treat this as a substring of a model name; for
      * example:
      *   - `claude-3-5-sonnet` should match `claude-3-5-sonnet-20241022`
      *   - `sonnet` should match `claude-3-5-sonnet-20241022`,
      *     `claude-3-sonnet-20240229`, etc.
      *   - `claude` should match any Claude model
      *
      * The client MAY also map the string to a different provider's model name
      * or a different model family, as long as it fills a similar niche; for
      * example:
      *   - `gemini-1.5-flash` could match `claude-3-haiku-20240307`
      */
    name: Option[String] = None
) derives ReadWriter
