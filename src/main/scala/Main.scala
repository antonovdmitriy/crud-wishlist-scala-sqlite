import akka.actor.CoordinatedShutdown
import akka.actor.CoordinatedShutdown.UnknownReason
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.Route
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.service.WishlistServiceImpl

import java.time.Instant
import scala.concurrent.duration.{Duration, DurationInt}
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

//    println("before sleep " + Instant.now() + " " + Thread.currentThread().getId)
//
//    Thread.sleep(5000)
//
//    println("after sleep " + Instant.now() + " " + Thread.currentThread().getId)

    // Send a GET request to /health endpoint
//    val request = HttpRequest(GET, uri = Uri("http://127.0.0.1:3000/health"))
//    val responseFuture = Http().singleRequest(request)
//    println(Await.result(responseFuture, 5.seconds))

//    CoordinatedShutdown(system).addJvmShutdownHook {
//      println("Server is shutting down...")
//    }

//    // Block until the server is shut down
//    println(
//      "before readline " + Instant.now() + " " + Thread.currentThread().getId
//    )
//    StdIn.readLine()
//
//    println(
//      "after readline " + Instant.now() + " " + Thread.currentThread().getId
//    )
//
//    bindingFuture
//      .flatMap(_.unbind())
//      .onComplete(_ => {
//        println(
//          "before terminate " + Instant.now() + " " + Thread.currentThread().getId
//        )
//        system.terminate()
//      })
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
