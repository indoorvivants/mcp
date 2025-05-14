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

  /** Add a request handler. If used multiple times for same request, the last
    * handler will be used.
    *
    * ```scala
    * MCPBuilder
    *   .create()
    *   .handle(initialize): req =>
    *     // req is of type `InitializeParams`
    *     InitializeResult(...)
    * ```
    *
    * The handler function will be executed asynchronously on the executor
    * configured for the MCPBuilder.
    *
    * For a full set of requests that can be handled, see subclasses of
    * [[MCPRequest]] trait.
    *
    * @param req
    *   request to handle
    * @param f
    *   handler function. In the body of the function, [[mcp.communicate]] is
    *   available to give you access to the ability to send requests and
    *   notifications from server to client
    * @return
    *   updated MCPBuilder instance with the notification handler installed or
    *   overwritten
    */
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

  /** Add a request handler. If used multiple times for same request, the last
    * handler will be used.
    *
    * ```scala
    * MCPBuilder
    *   .create()
    *   .handle(initialize): req =>
    *     // req is of type `InitializeParams`
    *     InitializeResult(...)
    * ```
    *
    * The handler function will be executed asynchronously on the executor
    * configured for the MCPBuilder.
    *
    * For a full set of requests that can be handled, see subclasses of
    * [[MCPRequest]] trait.
    *
    * @param req
    *   notification to handle (e.g. [[mcp.notifications.initialized]])
    * @param f
    *   handler function. In the body of the function, [[mcp.communicate]] is
    *   available to give you access to the ability to send requests and
    *   notifications from server to client
    * @return
    *   updated MCPBuilder instance with the notification handler installed or
    *   overwritten
    */
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
