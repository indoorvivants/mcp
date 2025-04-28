package mcp

import upickle.default.*
import annotation.targetName
import upickle.core.TraceVisitor.TraceException

class Builder[T] private (seq: Seq[(String, Reader[?])]):
  opaque type BuilderType <: T = T

  def orElse[T2](label: String, other: Reader[T2]) =
    new Builder[T2 | T]((label, other) +: seq)

  @targetName("orElse_given")
  def orElse[T2](label: String)(using reader: Reader[T2]) =
    new Builder[T2 | T]((label, reader) +: seq)

  object BuilderType:

    given codec: Reader[BuilderType] =
      val rev = seq.reverse
      upickle_hacks.badMerge(rev.head, rev.tail*)
  end BuilderType
end Builder

object Builder:
  def apply[T: Reader](label: String) =
    new Builder[T](Seq(label -> summon[Reader[T]]))

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
