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

trait ServerEndpoints[F[_]]:
  def requestHandlers: Map[
    String,
    ServerToClient[F] ?=> (ujson.Value) => F[ujson.Value | Error]
  ]

  def notificationHandlers: Map[
    String,
    ServerToClient[F] ?=> (ujson.Value) => F[Unit]
  ]
end ServerEndpoints

trait Transport[A]:
  type F[A]

  def run(endpoints: ServerEndpoints[F]): A
end Transport
