import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.Route
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.service.WishlistServiceImpl

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    // Instantiate dependencies
    val wishlistService = new WishlistServiceImpl

    // needed to run the route
    implicit val system: ActorSystem[_] =
      ActorSystem(Behaviors.empty, "wishlist-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContext = system.executionContext

    val route: Route = new WishlistController(wishlistService).route


    val bindingFuture = Http().newServerAt("127.0.0.1", 3000).bind(route)

    // Log the server startup message
    bindingFuture.foreach { binding =>
      println(
        s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/"
      )
    }


    // Send a GET request to /health endpoint
    val request = HttpRequest(GET, uri = Uri("http://127.0.0.1:3000/health"))
    val responseFuture = Http().singleRequest(request)
    println(Await.result(responseFuture, 5.seconds))

//    CoordinatedShutdown(system).addJvmShutdownHook {
//      println("Server is shutting down...")
//    }

    // Block until the server is shut down
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
