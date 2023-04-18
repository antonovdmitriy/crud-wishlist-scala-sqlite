import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.service.WishlistServiceImpl

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object Main {
  def main(args: Array[String]): Unit = {
    // Instantiate dependencies
    val wishlistService = new WishlistServiceImpl
    val route: Route = new WishlistController(wishlistService).route

    implicit val system: ActorSystem[_] =
      ActorSystem(Behaviors.empty, "wishlist-system")
    implicit val executionContext: ExecutionContext = system.executionContext
    val bindingFuture = Http().newServerAt("127.0.0.1", 3000).bind(route)

    // Log the server startup message
    bindingFuture.foreach { binding =>
      println(
        s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/"
      )
    }

    // Register a JVM shutdown hook for coordinated shutdown
    CoordinatedShutdown(system).addJvmShutdownHook {
      println("Server is shutting down...")
      bindingFuture
        .flatMap(_.unbind())
        .onComplete { _ =>
          system.terminate()
        }
    }

    // Block until a shutdown signal is received
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
