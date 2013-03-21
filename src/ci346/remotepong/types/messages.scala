package ci346.remotepong.types

import scala.actors.Actor

@serializable case class Point(x: Int, var y: Int)
@serializable case class Ball(x: Int, y: Int, xDir: Int, yDir: Int)
@serializable case class GameState(paddle1: Point, paddle2: Point, ball: Ball) 
@serializable case object StopMsg
@serializable case class GetStateMsg(isLeft: Boolean, yPos: Int)
case class StartMsg(isLeft: Boolean)
case class JoinMsg
@serializable case class Msg(m: String)
case class DirectionMsg(isLeft: Boolean)
