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

import scala.annotation.targetName
import scala.concurrent.Future

import mcp.*

import json.*

type Id[A] = A

class MCPBuilder private (opts: MCPBuilder.Opts):
  self =>
  import MCPBuilder.*
  private def copy(f: Opts => Opts) = new MCPBuilder(f(opts))

  type F[A] = Id[A]

  def handle(req: MCPRequest & FromClient)(
      f: ServerToClient[Id] ?=> req.In => req.Out | Error
  ): MCPBuilder =
    val handler = (c: ServerToClient[Id]) ?=>
      (in: ujson.Value) =>
        val params = read[req.In](in)
        f(params) match
          case e: Error => writeJs(e)
          case e        => writeJs(e.asInstanceOf[req.Out])

    copy(o =>
      o.copy(requestHandlers = o.requestHandlers.updated(req.method, handler))
    )
  end handle

  @targetName("handleNotification")
  def handle(req: MCPNotification & FromClient)(
      f: ServerToClient[Id] ?=> req.In => Unit
  ): MCPBuilder =
    val handler = (c: ServerToClient[Id]) ?=>
      (in: ujson.Value) =>
        val params = read[req.In](in)
        f(params)

    copy(o =>
      o.copy(notificationHandlers =
        o.notificationHandlers.updated(req.method, handler)
      )
    )
  end handle

  def run[A](transport: Transport[A] { type F[A] = self.F[A] }): A =
    transport.run(opts)
end MCPBuilder

object MCPBuilder:
  def create(): MCPBuilder =
    new MCPBuilder(
      Opts(
        Map.empty,
        Map.empty
      )
    )
  end create

  private case class Opts(
      requestHandlers: Map[
        String,
        ServerToClient[Id] ?=> (ujson.Value) => ujson.Value | Error
      ],
      notificationHandlers: Map[
        String,
        ServerToClient[Id] ?=> (ujson.Value) => Unit
      ]
  ) extends ServerEndpoints[Id]
end MCPBuilder
