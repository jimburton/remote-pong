package ci346.remotepong.server

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._
import ci346.remotepong.types._
import ci346.remotepong.types.Constants._

import scala.actors.OutputChannel


class Server(port: Int) extends Actor {
  
  val gs = new GameState(new Point(0, GAME_HEIGHT/2), 
      new Point(GAME_WIDTH, GAME_HEIGHT/2), new Ball(GAME_WIDTH/2, GAME_HEIGHT/2, 1, 1))

  override def act = {
    alive(port)
    register('Server, self)
    var players = 0
    while(true) {
      receive {
        case JoinMsg =>
          if (players == 0) {
            Console.println("got player 1")
            sender ! DirectionMsg(true)
            players += 1
          } else if (players == 1) {
            Console.println("got player 2")
            sender ! DirectionMsg(false)
            players += 1
          }
        case GetStateMsg(isLeft, yPos) =>
          //Console.println("got state request. From left? "+isLeft)
          if (isLeft) gs.paddle1.y = yPos else gs.paddle2.y = yPos 
          sender ! new GameState(new Point(0,0), new Point(0,0), new Ball(0, 0, 0, 0))
      }
    }
  }
}