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

import mcp.*

import json.*

inline def communicate[F[_], Origin <: FromServer | FromClient](using
    comm: Communicate[F, Origin]
) = comm

trait Communicate[F[_], Origin <: (FromServer | FromClient)]:
  def notification[X <: MCPNotification & Origin](notif: X)(
      in: notif.In
  ): F[Unit]

  def request[X <: MCPRequest & Origin](
      req: X,
      options: RequestOptions = RequestOptions.default
  )(
      in: req.In
  ): F[req.Out | Error]
end Communicate

trait ServerToClient[F[_]] extends Communicate[F, FromServer]
trait ClientToServer[F[_]] extends Communicate[F, FromClient]
