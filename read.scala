//> using dep com.lihaoyi::upickle::4.1.0
//> using dep com.lihaoyi::pprint::0.9.0

import upickle.default.*
import upickle.core.TraceVisitor.TraceException
import scala.annotation.targetName

given [T <: Singleton & String](using v: ValueOf[T]): Reader[T] =
  summon[Reader[String]].map: s =>
    val t = s.asInstanceOf[T]
    assert(t == v.value, s"Expected ${v.value} but got $t")
    t

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

enum MCPError(msg: String, tr: Throwable = null) extends Throwable(msg, tr):
  case FailureParsing(in: ujson.Value, reason: Throwable = null)
      extends MCPError(s"Failed to parse input '$in' $reason", reason)
  case StubError(msg: String) extends MCPError(msg)
end MCPError

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

object types:
  val MixedType = Builder[String]("string")
    .orElse[List[String]]("list")
  type MixedType = MixedType.BuilderType

  val DefDef = Builder[AnyOf]("anyof")
    .orElse[Definition]("definition")
    .orElse[Ref]("ref")

  type DefDef = DefDef.BuilderType

  case class Str(
      const: Option[String] = None,
      format: Option[String] = None,
      `type`: "string"
  ) derives Reader
  case class Arr(`type`: "array") derives Reader
  case class Num(`type`: "number") derives Reader
  case class Integer(`type`: "integer") derives Reader
  case class Obj(
      `type`: "object",
      properties: Map[String, Property] = Map.empty,
      required: List[String] = List.empty
  ) derives Reader
  case class Bool(`type`: "boolean") derives Reader
  case class Mixed(`type`: List[String]) derives Reader
  case class Data() derives Reader

  val Property = Builder[Str]("str")
    .orElse[Arr]("arr")
    .orElse[Num]("num")
    .orElse[Ref]("ref")
    .orElse[Integer]("integer")
    .orElse[Obj]("obj")
    .orElse[Bool]("bool")
    .orElse[AnyOf]("anyOf")
    .orElse[Mixed]("mixed")
    .orElse[Data]("data")

  type Property = Property.BuilderType

end types

import types.*

case class Definition(
    description: Option[String] = None,
    properties: Map[String, Property] = Map.empty,
    `type`: MixedType,
    required: List[String] = List.empty
) derives Reader

case class Ref(`$ref`: String, description: Option[String] = None)
    derives Reader

case class AnyOf(anyOf: List[DefDef]) derives Reader

case class Schema(
    definitions: Map[String, DefDef]
) derives Reader

@main def hello =
  val cont = io.Source.fromFile("./schema.json").getLines.mkString("\n")
  val schema = read[Schema](cont)

  println(pprint.pprintln(schema.definitions("LoggingMessageNotification")))

end hello
