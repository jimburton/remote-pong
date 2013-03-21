package ci346.remotepong.akka

object Messages {
  case object JoinMsg
  sealed trait Player
  case object Player1 extends Player
  case object Player2 extends Player
  case object Begin
  case class Msg(s: String)
  case class UpdateFromClient(y: Int, p: Player)
  case class Ball(var x: Int, var y: Int, var xDir: Int, var yDir: Int)
  case class UpdateFromServer(yPos: Int, b: Ball)
  case class Goal(p: Player)
}