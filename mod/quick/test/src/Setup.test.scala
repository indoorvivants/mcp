package mcp

import java.io.*
import java.util.concurrent.*
import ujson.Obj
import ujson.Value

trait Setup:
  protected case class Probe(
      out: ByteArrayOutputStream,
      err: ByteArrayOutputStream,
      executor: Executor,
      transport: SyncTransport
  ):
    def responsesById: Map[Value, Obj] = out
      .toString()
      .split("\n")
      .map(s => upickle.default.read[ujson.Obj](s))
      .flatMap: j =>
        j.value.get("id").map(_ -> j)
      .toMap

    def notifications =
      out
        .toString()
        .split("\n")
        .map(s => upickle.default.read[ujson.Obj](s))
        .collect:
          case j if !j.value.contains("id") && j.value.contains("method") => j
        .toSeq

  end Probe

  protected inline def withProbe[A](inputLines: List[String])(f: Probe => A) =
    f(probe(inputLines))

  protected def probe(inputLines: List[String]) =
    val in = new ByteArrayInputStream(inputLines.mkString("\n").getBytes)
    val out = new ByteArrayOutputStream
    val err = new ByteArrayOutputStream
    val executor = new Executor:
      override def execute(command: Runnable): Unit = command.run()

    Probe(
      out,
      err,
      executor,
      SyncTransport.default
        .out(out)
        .err(err)
        .in(in)
        .executor(executor)
        .verbose
    )
  end probe

  protected case class Opts(
      requestHandlers: Map[
        String,
        ServerToClient[Id] ?=> (ujson.Value) => ujson.Value | Error
      ] = Map.empty,
      notificationHandlers: Map[
        String,
        ServerToClient[Id] ?=> (ujson.Value) => Unit
      ] = Map.empty
  ) extends ServerEndpoints[Id]
end Setup
