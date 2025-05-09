package mcp

import mcp.json.*

/** A request from the client to the server, to ask for completion options.
  */
case class CompleteRequest(
    params: CompleteRequest.Params,
    method: "completion/complete" = "completion/complete"
) derives ReadWriter

object CompleteRequest:
  case class Params(
      /** The argument's information
        */
      argument: Params.Argument,
      ref: Params.Ref
  ) derives ReadWriter

  object Params:
    case class Argument(
        /** The name of the argument
          */
        name: String,
        /** The value of the argument to use for completion matching.
          */
        value: String
    ) derives ReadWriter

    val Ref =
      Builder[mcp.PromptReference]("PromptReference")
        .orElse[mcp.ResourceReference]("ResourceReference")

    type Ref = Ref.BuilderType
  end Params
end CompleteRequest
