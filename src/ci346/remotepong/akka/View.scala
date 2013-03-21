package ci346.remotepong.akka

import scala.swing._
import scala.swing.event._
import java.awt.Color
import ci346.remotepong.akka.Constants._
import ci346.remotepong.akka.Messages._

class View(p: Player) extends SimpleSwingApplication {

  var ball = new Ball(250, 250, 1, 1)
  var p1YPos = 250
  var p2YPos = 250
  def pYPos = p match {
    case Player1 => p1YPos
    case Player2 => p2YPos
  }
  
  def setYPos(pos: Int) = p match {
    case Player1 => p1YPos = pos
    case Player2 => p2YPos = pos
  }

  var inPlay = false
  var autoPilot = true

  def receive(oYPos: Int, b: Ball) = {
    p match {
      case Player1 => {
        println("I am Player 1, updating Player 2 yPos")
        p2YPos = oYPos
      }
      case Player2 => {
        println("I am Player 2, updating Player 1 yPos")
        p1YPos = oYPos
      }
      case unknown => println("Which player am I?!")
    }
    ball = b
    println("New ball with yPos: " + ball.y)
  }

  def startPoint = top.startPoint
  def endPoint = top.endPoint

  val top = new MainFrame {
    title = "Pong, As She is Played"
    val label = new Label {
      text = TXT_BEGIN
    }
    val checkbox = new CheckBox {
      text = "Autopilot"
      selected = autoPilot
    }
    def panel = new Component {
      border = javax.swing.BorderFactory.createLineBorder(Color.black)
      override def paintComponent(g: Graphics2D) = {
        super.paintComponent(g)
        if (inPlay) {
          g.setColor(Color.black)
          if (autoPilot) movePaddleToBall
          g.fillRect(P1_X, p1YPos, PADDLE_WIDTH, PADDLE_HEIGHT);
          g.fillRect(P2_X, p2YPos, PADDLE_WIDTH, PADDLE_HEIGHT);
          g.fillRect(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
        }
      }
      listenTo(keys)
      listenTo(checkbox)
      reactions += {
        case KeyPressed(_, Key.Up, _, _) =>
          if (!autoPilot && pYPos > 0) setYPos(pYPos - PADDLE_INC)
        case KeyReleased(_, Key.Down, _, _) =>
          if (!autoPilot && pYPos < GAME_HEIGHT) setYPos(pYPos + PADDLE_INC)
        case ButtonClicked(checkbox) => {
          autoPilot = !autoPilot
          this.requestFocusInWindow
        }
      }
      focusable = true
      requestFocus
    }
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += checkbox
        contents += label
      }
      contents += panel
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }
    panel.requestFocusInWindow
    val t = new javax.swing.Timer(UPDATE_DELAY, new java.awt.event.ActionListener {
      def actionPerformed(e: java.awt.event.ActionEvent) {
        //label.text = "paddle1: [" + paddle1.xPos + "," + paddle1.yPos + "] paddle2: [" + paddle2.xPos + "," + paddle2.yPos + "] ball: [" + ball.xPos + "," + ball.yPos + "]"
        repaint()
      }
    })
    minimumSize = new Dimension(650, 650)

    def startPoint = {
      inPlay = true
      t.start()
      println("Called t.start")
    }

    def endPoint = {
      t.stop()
      val y = GAME_HEIGHT / 2
      inPlay = false
    }

    def movePaddleToBall = {
      println("moving paddle to ball")
      p match {
        case Player1 =>
          if (p1YPos < ball.y) {
            p1YPos += PADDLE_INC
          } else if (p1YPos > ball.y) {
            p1YPos -= PADDLE_INC
          }
        case Player2 =>
          if (p2YPos < ball.y) {
            p2YPos += PADDLE_INC
          } else if (p2YPos > ball.y) {
            p2YPos -= PADDLE_INC
          }
      }

    }
  }
}
