package ci346.remotepong.akka

import akka.actor._
import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ Props, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory
import ci346.remotepong.akka.Constants._
import ci346.remotepong.akka.Messages._

class MyActor extends Actor {

  var inPlay = false
  var p1YPos = 0
  var p2YPos = 0
  var p1: ActorRef = null
  var p2: ActorRef = null
  val b = new Ball(GAME_WIDTH / 2, GAME_HEIGHT / 2, 1, 1)
  val t = new javax.swing.Timer(UPDATE_DELAY, new java.awt.event.ActionListener {
    def actionPerformed(e: java.awt.event.ActionEvent) {
      if (b.x > GAME_WIDTH) { //goal
        end(new Goal(Player1))
      } else if (b.x < 0) { //goal
        end(new Goal(Player2))
      }
      if (inPlay) {
        moveBall
        p1 ! new UpdateFromServer(p2YPos, b)
        p2 ! new UpdateFromServer(p1YPos, b)
      }
    }
  })

  def receive = {
    case JoinMsg =>
      println("got player 1")
      p1 = sender
      sender ! Player1
      context.become(waitingForPlayer2(sender))
  }

  def waitingForPlayer2(player1: ActorRef): Actor.Receive = {
    case JoinMsg =>
      println("got player 2")
      p2 = sender
      sender ! Player2
      player1 ! Begin
      sender ! Begin
      inPlay = true
      t.start()
      context.become(ready)
  }

  def ready: Actor.Receive = {
    case UpdateFromClient(yPos, p) => p match {
      case Player1 => {
        //println("Update from player 1: " + yPos)
        p1YPos = yPos
      }
      case Player2 => {
        //println("Update from player 2: " + yPos)
        p2YPos = yPos
      }
    }
    case unknown => println("Unknown message")
  }
  
  def end(g: Goal): Unit = {
    println("Goal")
    p1 ! g
    p2 ! g
    inPlay = false
    t.stop
  }
  
  def moveBall = {
    //println("Comparing ball ["+b.x+","+b.y+"] to y values ["+p1YPos+","+p2YPos+"]")
    if ((b.x < PADDLE_WIDTH + HALF_BALL) && (b.y <= p1YPos + HALF_HEIGHT)
      && (b.y >= p1YPos - HALF_HEIGHT) && b.xDir < 0) { //collision with p1
      b.xDir *= -1
    } else if ((b.x > GAME_WIDTH - PADDLE_WIDTH - HALF_BALL) && b.y <= p2YPos + HALF_HEIGHT
      && b.y >= p2YPos - HALF_HEIGHT && b.xDir > 0) { //collision with p2
      b.xDir *= -1
    } else if (b.y > GAME_HEIGHT || b.y < HALF_BALL) { //bounced off top or bottom
      b.yDir *= -1
    }
    b.x += BALL_INC * b.xDir
    b.y += BALL_INC * b.yDir
  }
}

class ServerApplication extends Bootable {

  val system = ActorSystem("Game", ConfigFactory.load("application"))
  val actor = system.actorOf(Props[MyActor], "server")

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

object ServerApp {
  def main(args: Array[String]) {
    new ServerApplication
    println("Started AkkaDemo Application - waiting for messages")
  }
}
