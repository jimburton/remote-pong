package ci346.remotepong.client

import scala.actors.remote.Node
import scala.actors.remote.RemoteActor
import ci346.remotepong.server.Server

object clientapp {
  def main(args: Array[String]): Unit = {
    RemoteActor.classLoader = getClass().getClassLoader()
    val port = args(0).toInt
    val peer = Node(args(1), args(2).toInt)
    val client = new Client(port, peer, 16)
    client.start()
  }
}