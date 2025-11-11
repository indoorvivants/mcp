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

/** Describes an argument that a prompt can accept.
  */
case class PromptArgument(
    /** Intended for programmatic or logical use, but used as a display name in
      * past specs or fallback (if title isn't present).
      */
    name: String,
    /** A human-readable description of the argument.
      */
    description: Option[String] = None,
    required: Option[Boolean] = None,
    /** Intended for UI and end-user contexts — optimized to be human-readable
      * and easily understood, even by those unfamiliar with domain-specific
      * terminology.
      *
      * If not provided, the name should be used for display (except for Tool,
      * where `annotations.title` should be given precedence over using `name`,
      * if present).
      */
    title: Option[String] = None
) derives ReadWriter
