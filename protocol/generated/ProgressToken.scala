package mcp

import mcp.json.*

/** A progress token, used to associate progress notifications with the original
  * request.
  */
val ProgressToken = Builder[String]("String")
  .orElse[Int]("Int")

type ProgressToken = ProgressToken.BuilderType
