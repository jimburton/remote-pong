package ci346.remotepong.client

import scala.swing._
import scala.swing.event._
import java.awt.Color
import ci346.remotepong.types._

class View(app: Client, isLeft: Boolean) extends SimpleSwingApplication {

  val GAME_WIDTH = 500
  val GAME_HEIGHT = 559
  val PADDLE_WIDTH = 20
  val PADDLE_HEIGHT = 50
  val HALF_HEIGHT = PADDLE_HEIGHT / 2
  val BALL_SIZE = 20
  val HALF_BALL = BALL_SIZE / 2
  val PADDLE_INC = 10
  val BALL_INC = 10
  val REPAINT_DELAY = 100
  val TXT_BEGIN = "Waiting for game to begin"

  class Sprite(var xPos: Int, var yPos: Int)

  class Ball(xPos: Int, yPos: Int) extends Sprite(xPos: Int, yPos: Int) {
    var xDir = 1
    var yDir = 1
  }

  val ball = new Ball(250, 250)
  val paddle1 = new Sprite(0, 300)
  val paddle2 = new Sprite(500, 300)

  def playerPaddle = if (isLeft) paddle1 else paddle2
  def opponentPaddle = if (!isLeft) paddle1 else paddle2

  var inPlay = false
  var autoPilot = false

  def start = {
    inPlay = true
  }

  def receive(gs: GameState) = {
    paddle1.xPos = gs.paddle1.x
    paddle1.yPos = gs.paddle1.y
    paddle2.xPos = gs.paddle2.x
    paddle2.yPos = gs.paddle2.y
    ball.xPos = gs.ball.x
    ball.yPos = gs.ball.y
    ball.xDir = gs.ball.xDir
    ball.yDir = gs.ball.yDir
    top.repaint
  }

  def sendUpdate = {
    app.update(new GameState(spriteToPoint(paddle1), spriteToPoint(paddle2), getBall))
  }

  def spriteToPoint(s: Sprite) = {
    new ci346.remotepong.types.Point(s.xPos, s.yPos)
  }

  def getBall = {
    new ci346.remotepong.types.Ball(ball.xPos, ball.yPos, ball.xDir, ball.yDir)
  }

  def top = new MainFrame {
    title = "Pong, As She is Played"
    //defaultCloseOperation = JFrame.EXIT_ON_CLOSE)
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
          if (autoPilot) movePaddleToBall(playerPaddle)
          g.fillRect(paddle1.xPos, paddle1.yPos, PADDLE_WIDTH, PADDLE_HEIGHT);
          g.fillRect(paddle2.xPos, paddle2.yPos, PADDLE_WIDTH, PADDLE_HEIGHT);
          g.fillRect(ball.xPos, ball.yPos, BALL_SIZE, BALL_SIZE);
        }
      }
      listenTo(keys)
      listenTo(checkbox)
      reactions += {
        case KeyPressed(_, Key.Up, _, _) =>
          if (!autoPilot && paddle1.yPos > 0) {
            playerPaddle.yPos -= PADDLE_INC
            sendUpdate
          }
        case KeyReleased(_, Key.Down, _, _) =>
          if (!autoPilot && paddle1.yPos < GAME_HEIGHT) {
            playerPaddle.yPos += PADDLE_INC
            sendUpdate
          }
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
    minimumSize = new Dimension(650, 650)
    val t = new javax.swing.Timer(REPAINT_DELAY, new java.awt.event.ActionListener {
      def actionPerformed(e: java.awt.event.ActionEvent) {
        label.text = "paddle1: [" + paddle1.xPos + "," + paddle1.yPos + "] paddle2: [" + paddle2.xPos + "," + paddle2.yPos + "] ball: [" + ball.xPos + "," + ball.yPos + "]"
        repaint
      }
    })

    def startPoint = {
      inPlay = true
      t.start
    }
    def endPoint = {
      t.stop()
      label.text = TXT_BEGIN
      val y = GAME_HEIGHT / 2
      paddle1.yPos = y
      paddle2.yPos = y
      ball.yPos = y
      ball.xPos = y
      ball.xDir = 1
      ball.yDir = 1
      inPlay = false
    }

    def movePaddleToBall(p: Sprite) = {
      if (p.yPos < ball.yPos) {
        p.yPos += PADDLE_INC
      } else if (p.yPos > ball.yPos) {
        p.yPos -= PADDLE_INC
      }
      sendUpdate
    }
  }

}
