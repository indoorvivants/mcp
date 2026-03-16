package mcp

import scala.annotation.StaticAnnotation
import ujson.Obj

// trait JsonSchema[T]:
//   def jsonSchema: ujson.Obj

// object JsonSchema:
//   given JsonSchema[String] with
//     def jsonSchema: Obj = ujson.Obj("type" -> "string")

//   given JsonSchema[Int] with
//     def jsonSchema: Obj = ujson.Obj("type" -> "integer")
// end JsonSchema

trait ToolInput[T]:
  def schema: ujson.Obj
  def read(json: ujson.Value): T
  def name: String
  def description: String
end ToolInput

object ToolInput:
  private[mcp] trait ToolAnnotation
  class Name(val name: String) extends StaticAnnotation, ToolAnnotation
  class Description(val description: String)
      extends StaticAnnotation,
        ToolAnnotation

  import deriving.*
  inline def derived[T](using Mirror.ProductOf[T]): ToolInput[T] =
    ${ derivedMacro[T] }

  import quoted.*
  def derivedMacro[T: Type](using Quotes): Expr[ToolInput[T]] =
    val ev: Expr[Mirror.ProductOf[T]] = Expr.summon[Mirror.ProductOf[T]].get

    import quotes.reflect.*

    val derivedAnnot = TypeRepr.of[ToolAnnotation]

    def hints(annotations: List[Term]): Hints =
      annotations
        .foldLeft(Hints()): (hints, ann) =>
          if ann.tpe <:< TypeRepr.of[Name] then
            hints.copy(name = Some(ann.asExprOf[Name].valueOrAbort.name))
          else if ann.tpe <:< TypeRepr.of[Description] then
            hints.copy(description =
              Some(ann.asExprOf[Description].valueOrAbort.description)
            )
          else hints

    ev match
      case '{
            $m: Mirror.ProductOf[T] {
              type MirroredElemTypes = elementTypes;
              type MirroredElemLabels = labels
              type MirroredLabel = commandName
              type MirroredType = t
            }

          } =>
        val cmdHints =
          hints(
            TypeRepr
              .of[T]
              .typeSymbol
              .annotations
          )

        val fieldNamesAndAnnotations: List[(String, Hints)] =
          TypeRepr
            .of[T]
            .typeSymbol
            .primaryConstructor
            .paramSymss
            .flatten
            .map: sym =>
              (
                sym.name,
                hints(sym.annotations)
              )

        ???

    end match
  end derivedMacro

  private def constructOption[E: Type](
      name: String,
      hints: Hints
  )(using Quotes): Expr[ToolInput[Any]] =
    import quotes.reflect.*

    val hasToolInput = Implicits.search(TypeRepr.of[ToolInput[E]]) match
      case res: ImplicitSearchSuccess =>
        Some(res.tree.asExprOf[ToolInput[E]])
      case _ => None

    val nm = hints.name match
      case None        => Expr(name)
      case Some(value) => Expr(value)

    val description = hints.description.fold(Expr(""))(Expr.apply)

    Type.of[E] match
      // case '[e] if isEnum && hasCommand.isDefined =>
      //   '{
      //     Opts.subcommands(
      //       ${ hasCommand.get }.subcommands.head,
      //       ${ hasCommand.get }.subcommands.tail*
      //     )
      //   }
      case '[Boolean] =>
        ???

    end match

      // case '[Option[e]] =>
      //   '{ ${ constructOption[e](name, hints) }.orNone }

      // case '[NonEmptyList[e]] =>
      //   val param = summonArgument[e](name)

      //   hints.positional match
      //     case None =>
      //       '{
      //         given Argument[e] = $param
      //         Opts.options[e](
      //           long = $nm,
      //           help = $help,
      //           short = $short
      //         )
      //       }
      //     case Some(value) =>
      //       val metavar = Expr(value)
      //       '{
      //         given Argument[e] = $param
      //         Opts.arguments[e](metavar = $metavar)
      //       }

      //   end match

      // case '[List[e]] =>
      //   '{
      //     ${ constructOption[NonEmptyList[e]](name, hints) }
      //       .map(_.asInstanceOf[NonEmptyList[e]].toList)
      //   }

      // case '[Set[e]] =>
      //   '{
      //     ${ constructOption[List[e]](name, hints) }
      //       .map(_.asInstanceOf[List[e]].toSet)
      //   }

      // case '[Vector[e]] =>
      //   '{
      //     ${ constructOption[List[e]](name, hints) }
      //       .map(_.asInstanceOf[List[e]].toVector)
      //   }

      // case '[Array[e]] =>
      //   val ct = Expr
      //     .summon[ClassTag[e]]
      //     .getOrElse(
      //       report.errorAndAbort(
      //         s"No ClassTag available for ${TypeRepr.of[e].show}"
      //       )
      //     )

      //   '{
      //     given ClassTag[e] = $ct
      //     ${ constructOption[List[e]](name, hints) }
      //       .map(_.asInstanceOf[List[e]].toArray)
      //   }

      // case '[e] =>
      //   val param = summonArgument[E](name)

      //   val base = hints.positional match
      //     case None =>
      //       '{
      //         Opts.option[E](
      //           $nm,
      //           $help,
      //           short = $short
      //         )(using $param)
      //       }
      //     case Some(value) =>
      //       val metaver = Expr(value)
      //       '{ Opts.argument[E](metavar = $metaver)(using $param) }
      //   end base

      //   hints.env match
      //     case None =>
      //       base
      //     case Some((name, help)) =>
      //       val envName = Expr(name)
      //       val envHelp = Expr(help)
      //       '{
      //         $base.orElse(
      //           Opts.env[E](name = $envName, help = $envHelp)(using $param)
      //         )
      //       }
      //   end match
      case _ =>
        report.errorAndAbort(
          s"Don't know how to handle type ${TypeRepr.of[E].show}"
        )
    end match
  end constructOption

  private def fieldOpts[T: Type](
      annots: List[(String, Hints)]
  )(using Quotes): List[Expr[Opts[?]]] =
    Type.of[T] match
      case ('[elem *: elems]) =>
        val nm = annots.head._1

        constructOption[elem](nm, annots.head._2) ::
          fieldOpts[elems](
            annots.tail
          )

      case other =>
        Nil
  end fieldOpts

  private def summonInstances[T: Type, Elems: Type](using
      Quotes
  ): List[Expr[ToolInput[?]]] =
    Type.of[Elems] match
      case '[elem *: elems] =>
        deriveOrSummon[T, elem].asInstanceOf :: summonInstances[T, elems]
      case '[EmptyTuple] => Nil

  private def deriveOrSummon[T: Type, Elem: Type](using
      Quotes
  ): Expr[ToolInput[Elem]] =
    Type.of[Elem] match
      case '[T] => deriveRec[T, Elem]
      case _    => '{ compiletime.summonInline[ToolInput[Elem]] }

  private def deriveRec[T: Type, Elem: Type](using
      Quotes
  ): Expr[ToolInput[Elem]] =
    Type.of[T] match
      case '[Elem] => '{ compiletime.error("infinite recursive derivation") }
      case _       => derivedMacro[Elem] // recursive derivation

  private given FromExpr[Name] with
    def unapply(x: Expr[Name])(using Quotes): Option[Name] =
      x match
        case '{ new Name($value) } => Some(Name(value.valueOrAbort))
        case _                     => None
  end given

  private given FromExpr[Description] with
    def unapply(x: Expr[Description])(using Quotes): Option[Description] =
      x match
        case '{ new Description($value) } =>
          Some(Description(value.valueOrAbort))
        case _ => None
  end given

  private final case class Hints(
      name: Option[String] = None,
      description: Option[String] = None
  )
end ToolInput
