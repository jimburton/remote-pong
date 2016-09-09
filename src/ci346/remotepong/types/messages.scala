package ci346.remotepong.types

import scala.actors.Actor

case class Point(x: Int, var y: Int) extends Serializable
case class Ball(x: Int, y: Int, xDir: Int, yDir: Int) extends Serializable
case class GameState(paddle1: Point, paddle2: Point, ball: Ball)  extends Serializable
case object StopMsg extends Serializable
case class GetStateMsg(isLeft: Boolean, yPos: Int) extends Serializable
case class StartMsg(isLeft: Boolean)
case class JoinMsg()
case class Msg(m: String) extends Serializable
case class DirectionMsg(isLeft: Boolean)
