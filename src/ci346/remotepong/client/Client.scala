package ci346.remotepong.client
import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.Exit
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node
import ci346.remotepong.types._
import scala.actors.AbstractActor


class Client(port: Int, peer: Node, count: Int) extends Actor with Serializable {
  trapExit = true // (1)
  var view: View = null
  var server: AbstractActor = null
  val UPDATE_DELAY = 50

  def startGame(isLeft: Boolean) = {
    view = new View(this, isLeft)
    view.main(null)
    new javax.swing.Timer(UPDATE_DELAY, new java.awt.event.ActionListener {
      def actionPerformed(e: java.awt.event.ActionEvent) {
        server ! new GetStateMsg(isLeft, view.playerPaddle.yPos)
      }
    }).start()
  }
  
  def update(gs: GameState) = {
    server ! gs
  }
  
  def act() {
    alive(port) // (2)
    register('Client, self) // (3)

    server = select(peer, 'Server) // (4)
    link(server) // (5)

    var pingsLeft = count - 1
    val msg = new Point(1, 2)
    server ! JoinMsg // (6)
    while (true) {
      receive { // (7)
        case Msg(s) =>
          Console.println(s)
        case DirectionMsg(isL) =>
          startGame(isL)
        case g@GameState(p1, p2, b) =>
          Console.println("got state")
          view.receive(g)
        case Exit(server, 'normal) => // (9)
          Console.println("stop")
          exit()
        case unknown =>
          Console.println("Got unknown message: "+unknown)
      }
    }

  }
}