package ci346.remotepong.client

import scala.swing._
import scala.swing.event._
import java.awt.Color

class Sprite(var xPos: Int, var yPos: Int)

class Ball(xPos: Int, yPos: Int) extends Sprite(xPos: Int, yPos: Int) {
  var xDir = 1
  var yDir = 1
}

object PongView extends SimpleSwingApplication {

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
  val TXT_BEGIN = "Click anywhere to start the game"

  val ball = new Ball(250, 250)
  val paddle1 = new Sprite(0, 300)
  val paddle2 = new Sprite(500, 300)

  var inPlay = false

  def top = new MainFrame {
    title = "Pong, As She is Played"
    //defaultCloseOperation = JFrame.EXIT_ON_CLOSE)
    val panel = new Component {
      border = javax.swing.BorderFactory.createLineBorder(Color.black)
      override def paintComponent(g: Graphics2D) = {
        super.paintComponent(g)
        if (inPlay) {
          g.setColor(Color.black)
          g.fillRect(paddle1.xPos, paddle1.yPos, PADDLE_WIDTH, PADDLE_HEIGHT);

          movePaddleToBall(paddle2)
          g.fillRect(paddle2.xPos, paddle2.yPos, PADDLE_WIDTH, PADDLE_HEIGHT);

          if ((ball.xPos < PADDLE_WIDTH + HALF_BALL) && (ball.yPos <= paddle1.yPos + HALF_HEIGHT)
            && (ball.yPos >= paddle1.yPos - HALF_HEIGHT) && ball.xDir < 0) { //collision with paddle1
            ball.xDir *= -1
          } else if ((ball.xPos > GAME_WIDTH - PADDLE_WIDTH - HALF_BALL) && ball.yPos <= paddle2.yPos + HALF_HEIGHT
            && ball.yPos >= paddle2.yPos - HALF_HEIGHT && ball.xDir > 0) { //collision with paddle2
            ball.xDir *= -1
          } else if (ball.xPos > GAME_WIDTH || ball.xPos < 0) { //goal
            endPoint
          } else if (ball.yPos > GAME_HEIGHT || ball.yPos < HALF_BALL) { //bounced off top or bottom
            ball.yDir *= -1
          }
          ball.xPos += BALL_INC * ball.xDir
          ball.yPos += BALL_INC * ball.yDir

          g.fillRect(ball.xPos, ball.yPos, BALL_SIZE, BALL_SIZE);
        }
      }
      listenTo(keys)
      listenTo(mouse.clicks)
      reactions += {
        case e: MouseClicked =>
          if (!inPlay) startPoint
        case KeyPressed(_, Key.Up, _, _) =>
          if (paddle1.yPos > 0) paddle1.yPos -= PADDLE_INC
        case KeyReleased(_, Key.Down, _, _) =>
          if (paddle1.yPos < GAME_HEIGHT) paddle1.yPos += PADDLE_INC
      }
      focusable = true
      requestFocus

    }
    val label = new Label {
      text = TXT_BEGIN
    }
    contents = new BoxPanel(Orientation.Vertical) {
      contents += label
      contents += panel
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }
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
    }
  }

}
