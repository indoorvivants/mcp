package mcp

import upickle.default.*
import annotation.targetName
import upickle.core.TraceVisitor.TraceException
import scala.reflect.TypeTest

class Builder[T] private (seq: Seq[(String, Any => Boolean, ReadWriter[?])]):
  opaque type BuilderType >: T = T

  @targetName("orElse_given")
  def orElse[T2](
      label: String
  )(using reader: ReadWriter[T2], tt: TypeTest[T | T2, T2]) =
    new Builder[T2 | T](
      (
        label,
        t2 => tt.unapply(t2.asInstanceOf[T | T2]).isDefined,
        reader
      ) +: seq
    )

  def embed[A <: T](value: A): BuilderType = value

  object BuilderType:

    given reader: Reader[BuilderType] =
      val rev = seq.reverse.map(s => (s._1, s._3))
      upickle_hacks.badMerge(rev.head, rev.tail*)

    given writer: Writer[BuilderType] =
      upickle_hacks.valueReader.comap[BuilderType]: bt =>
        val (label, _, writer) = seq.find(_._2.apply(bt)).get
        writeJs[BuilderType](bt)(using writer.asInstanceOf[Writer[BuilderType]])

  end BuilderType
end Builder

object Builder:
  def apply[T: ReadWriter](label: String) =
    new Builder[T](Seq.empty).orElse[T](label)

given [T <: Singleton & String](using v: ValueOf[T]): ReadWriter[T] =
  summon[ReadWriter[String]].bimap[T](
    t => t.toString,
    s =>
      assert(s == v.value, "Expected " + v.value + " but got " + s)
      v.value
  )

object upickle_hacks:
  val valueReader = upickle.default.readwriter[ujson.Value]
  def badMerge[T](
      r1: => (String, Reader[?]),
      rest: (String, Reader[?])*
  ): Reader[T] =
    valueReader.map { json =>
      var t = Option.empty[T]
      val stack = Map.newBuilder[String, Throwable]

      (r1 +: rest).foreach { reader =>
        if t.isEmpty then
          try
            t = Some(
              read[T](json, trace = true)(using
                reader._2.asInstanceOf[Reader[T]]
              )
            )
          catch
            case exc: TraceException =>
              stack += reader._1 -> exc.getCause()
            case exc => stack += reader._1 -> exc
      }
      t.getOrElse(
        throw new Throwable(
          s"Failed to parse $json: ${stack.result().mkString("\n", "\n", "")}"
        )
      )
    }

  extension [T](r: Reader[T]) def widen[K >: T] = r.map(_.asInstanceOf[K])

end upickle_hacks
