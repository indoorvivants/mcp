package mcp

import upickle.default.*
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

class SyncTransportTest extends munit.FunSuite, Setup:
  test("handlers basics"):
    val notifs = collection.mutable.Map.empty[String, ujson.Value]

    val handle = Opts(
      requestHandlers = Map(
        "hello/world" -> handleReq(_ => ujson.Obj("hello" -> "world")),
        "bye/juice" -> handleReq(params =>
          ujson.Obj("bye" -> params("bye"), "ok" -> true)
        )
      ),
      notificationHandlers = Map(
        "notif/one" -> handleNotif(params =>
          notifs.addOne("notif/one" -> params)
        ),
        "notif/two" -> handleNotif(params =>
          notifs.addOne("notif/two" -> params)
        )
      )
    )

    val r1 = req("hello/world", ujson.Obj())
    val r2 = req("bye/juice", ujson.Obj("bye" -> "tony"))
    val n1 = notif("notif/one", ujson.Obj("bye" -> "tony"))
    val n2 = notif("notif/two", ujson.Obj("hello" -> "world"))

    withProbe(
      lines(r1, r2, n1, n2)
    ): probe =>
      probe.transport.run(handle)
      assertEquals(
        probe.responsesById(r1("id"))("result"),
        ujson.Obj("hello" -> "world")
      )
      assertEquals(
        probe.responsesById(r2("id"))("result"),
        ujson.Obj("bye" -> "tony", "ok" -> true)
      )

      assertEquals(notifs("notif/one"), n1("params"))
      assertEquals(notifs("notif/two"), n2("params"))

  test("unhandled methods"):
    val r1 = req("hello/world", ujson.Obj())
    withProbe(lines(r1)): probe =>
      probe.transport.run(Opts())
      val resp = probe.responsesById(r1("id"))

      assertEquals(
        resp("error")("code").num.toInt,
        ErrorCode.MethodNotFound.value
      )

      assertEquals(
        resp("error")("message").str,
        s"Method ${r1("method").str} is not handled"
      )

  test("broken json"):
    withProbe(List("hello")): probe =>
      probe.transport.run(Opts())
      assert(
        probe.err.toString.contains("Failed to parse JSON"),
        probe.err.toString
      )

  test("exception in handler"):
    val r1 = req("hello/world", ujson.Obj())
    withProbe(lines(r1)): probe =>
      probe.transport.run(
        Opts(requestHandlers =
          Map("hello/world" -> handleReq(_ => sys.error("oh noes")))
        )
      )
      val resp = probe.responsesById(r1("id"))

      assertEquals(
        resp("error")("code").num.toInt,
        ErrorCode.InternalError.value
      )

      assertEquals(
        resp("error")("message").str,
        s"oh noes"
      )

  test("sending notifications"):
    val r1 = req("hello/world")
    val r2 = ujson.Obj("id" -> 1, "jsonrpc" -> "2.0", "result" -> ujson.Obj())

    object myReq extends MCPNotification("hello/client"), FromServer:
      override type In = ujson.Obj

    val handle = Opts(
      Map(
        "hello/world" -> (comm ?=>
          _ =>
            communicate.notification(myReq)(ujson.Obj("a" -> "b"))
            ujson.Obj()
        )
      )
    )

    withProbe(lines(r1, r2)): probe =>
      probe.transport.run(handle)
      assertEquals(
        probe.notifications,
        Seq(
          ujson.Obj(
            "method" -> "hello/client",
            "jsonrpc" -> "2.0",
            "params" -> ujson.Obj("a" -> "b")
          )
        )
      )

  def lines(obj: ujson.Obj*) =
    obj.map(write(_)).toList

  def req(method: String, params: ujson.Obj = ujson.Obj()) =
    val id = ujson.Num(serverRequestCounter.getAndIncrement)
    ujson.Obj(
      "id" -> id,
      "method" -> method,
      "params" -> params,
      "jsonrpc" -> "2.0"
    )
  end req

  def notif(method: String, params: ujson.Obj) =
    val id = ujson.Num(serverRequestCounter.getAndIncrement)
    ujson.Obj(
      "method" -> method,
      "params" -> params,
      "jsonrpc" -> "2.0"
    )
  end notif

  protected val serverRequestCounter = new AtomicInteger(1)

  private def handleReq(f: ujson.Value => ujson.Value | Error) =
    (_: ServerToClient[Id]) ?=> f

  private def handleNotif(f: ujson.Value => Unit) =
    (_: ServerToClient[Id]) ?=> f
end SyncTransportTest
