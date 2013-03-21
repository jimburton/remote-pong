package ci346.remotepong.akka

import akka.actor._
import com.typesafe.config.ConfigFactory
import ci346.remotepong.akka.Constants._
import ci346.remotepong.akka.Messages._

object Client {

  val system = ActorSystem("Game", ConfigFactory.load("client"))
  val server = system.actorFor("akka://Game@localhost:9000/user/server")
  var p: Player = null
  var v: View = null
  val t = new javax.swing.Timer(UPDATE_DELAY, new java.awt.event.ActionListener {
    def actionPerformed(e: java.awt.event.ActionEvent) {
      server ! new UpdateFromClient(v.pYPos, p)
    }
  })

  def main(args: Array[String]): Unit = {
    system.actorOf(Props(new Actor {
      server ! JoinMsg
      def receive = {
        case Msg(s) => println(s)
        case pl: Player => p = pl; pl match {
          case Player1 => println("I am Player 1")
          case Player2 => println("I am Player 2")
        }
        case Begin => println("Begin");begin
        case UpdateFromServer(y, b) => println("Got update from server: "+y+", "+b.y+", "+b.x);v.receive(y, b)
        case Goal(p) => println("Goal");end
      }
    }))
  }
  
  def end = {
    v.endPoint
    t.stop
  }
  
  def begin = {
    println("start sending updates")
    v = new View(p)
    v.main(null)
    v.startPoint
    t.start
  }
}