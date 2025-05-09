package mcp

import mcp.json.*
import scala.annotation.targetName

type Id[A] = A

class MCPBuilder private (opts: MCPBuilder.Opts):
  self =>
  import MCPBuilder.*
  private def copy(f: Opts => Opts) = new MCPBuilder(f(opts))

  def handle(req: MCPRequest & FromClient)(
      f: Communicate[Id, FromServer] ?=> req.In => req.Out | Error
  ): MCPBuilder =
    val handler = (c: Communicate[Id, FromServer]) ?=>
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
      f: Communicate[Id, FromServer] ?=> req.In => Unit
  ): MCPBuilder =
    val handler = (c: Communicate[Id, FromServer]) ?=>
      (in: ujson.Value) =>
        val params = read[req.In](in)
        f(params)

    copy(o =>
      o.copy(notificationHandlers =
        o.notificationHandlers.updated(req.method, handler)
      )
    )
  end handle

  def run[A](transport: Transport[A] { type F[X] = X }): A =
    transport.run(
      ServerEndpoints(opts.requestHandlers, opts.notificationHandlers)
    )
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
        Communicate[Id, FromServer] ?=> (ujson.Value) => ujson.Value | Error
      ],
      notificationHandlers: Map[
        String,
        Communicate[Id, FromServer] ?=> (ujson.Value) => Unit
      ]
  )
end MCPBuilder
