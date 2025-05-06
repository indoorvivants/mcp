package mcp

trait Transport[A]:
  type F[A]

  def run(
      requestHandlers: Map[
        String,
        Communicate[F, FromServer] ?=> (ujson.Value) => F[ujson.Value | Error]
      ],
      notificationHandlers: Map[
        String,
        Communicate[F, FromServer] ?=> (ujson.Value) => F[Unit]
      ]
  ): A
end Transport
