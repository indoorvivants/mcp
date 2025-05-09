package mcp

class ServerEndpoints[F[_]](
    val requestHandlers: Map[
      String,
      Communicate[F, FromServer] ?=> (ujson.Value) => F[ujson.Value | Error]
    ],
    val notificationHandlers: Map[
      String,
      Communicate[F, FromServer] ?=> (ujson.Value) => F[Unit]
    ]
)

trait Transport[A]:
  type F[A]

  def run(endpoints: ServerEndpoints[F]): A
end Transport
