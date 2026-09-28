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

/** Execution-related properties for a tool.
  */
case class ToolExecution(
    /** Indicates whether this tool supports task-augmented execution. This
      * allows clients to handle long-running operations through polling the
      * task system.
      *
      *   - "forbidden": Tool does not support task-augmented execution (default
      *     when absent)
      *   - "optional": Tool may support task-augmented execution
      *   - "required": Tool requires task-augmented execution
      *
      * Default: "forbidden"
      */
    taskSupport: Option[String] = None
) derives ReadWriter
