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

import scala.reflect.TypeTest

import upickle.core.TraceVisitor.TraceException

import annotation.targetName

object json extends upickle.AttributeTagged:
  override def optionsAsNulls: Boolean = true
  override def serializeDefaults: Boolean = true
end json

import json.*

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
  val valueReader = json.readwriter[ujson.Value]
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
