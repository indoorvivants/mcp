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

/** The client's response to an elicitation request.
  */
case class ElicitResult(
    /** The user action in response to the elicitation.
      *   - "accept": User submitted the form/confirmed the action
      *   - "decline": User explicitly declined the action
      *   - "cancel": User dismissed without making an explicit choice
      */
    action: String,
    /** See [General fields:
      * `_meta`](/specification/2025-06-18/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** The submitted form data, only present when action is "accept". Contains
      * values matching the requested schema.
      */
    content: Option[ujson.Obj] = None
) derives ReadWriter
