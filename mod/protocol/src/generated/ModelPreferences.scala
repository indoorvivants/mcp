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

/** The server's preferences for model selection, requested of the client during
  * sampling.
  *
  * Because LLMs can vary along multiple dimensions, choosing the "best" model
  * is rarely straightforward. Different models excel in different areas—some
  * are faster but less capable, others are more capable but more expensive, and
  * so on. This interface allows servers to express their priorities across
  * multiple dimensions to help clients make an appropriate selection for their
  * use case.
  *
  * These preferences are always advisory. The client MAY ignore them. It is
  * also up to the client to decide how to interpret these preferences and how
  * to balance them against other considerations.
  */
case class ModelPreferences(
    /** How much to prioritize cost when selecting a model. A value of 0 means
      * cost is not important, while a value of 1 means cost is the most
      * important factor.
      */
    costPriority: Option[Double] = None,
    /** Optional hints to use for model selection.
      *
      * If multiple hints are specified, the client MUST evaluate them in order
      * (such that the first match is taken).
      *
      * The client SHOULD prioritize these hints over the numeric priorities,
      * but MAY still use the priorities to select from ambiguous matches.
      */
    hints: Option[Seq[mcp.ModelHint]],
    /** How much to prioritize intelligence and capabilities when selecting a
      * model. A value of 0 means intelligence is not important, while a value
      * of 1 means intelligence is the most important factor.
      */
    intelligencePriority: Option[Double] = None,
    /** How much to prioritize sampling speed (latency) when selecting a model.
      * A value of 0 means speed is not important, while a value of 1 means
      * speed is the most important factor.
      */
    speedPriority: Option[Double] = None
) derives ReadWriter
