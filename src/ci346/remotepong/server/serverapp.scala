package ci346.remotepong.server

import scala.actors.remote.RemoteActor

object serverapp {
  def main(args: Array[String]): Unit = {
    RemoteActor.classLoader = getClass().getClassLoader()
    val port = args(0).toInt
    val server = new Server(port)
    server.start()
  }
}