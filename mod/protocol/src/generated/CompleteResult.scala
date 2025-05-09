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

/** The server's response to a completion/complete request
  */
case class CompleteResult(
    completion: CompleteResult.Completion,
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None
) derives ReadWriter

object CompleteResult:
  case class Completion(
      values: Seq[String],
      hasMore: Option[Boolean] = None,
      /** The total number of completion options available. This can exceed the
        * number of values actually sent in the response.
        */
      total: Option[Int] = None
  ) derives ReadWriter
end CompleteResult
