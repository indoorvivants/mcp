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

/** A notification from the server to the client, informing it that a resource
  * has changed and may need to be read again. This should only be sent if the
  * client previously sent a resources/subscribe request.
  */
case class ResourceUpdatedParams(
    /** The URI of the resource that has been updated. This might be a
      * sub-resource of the one that the client actually subscribed to.
      */
    uri: String
) derives ReadWriter
