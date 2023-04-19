import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.persistence.{DataSourceBuilder, DatabaseInitializer, HikariCPDataSourceBuilder, SqliteDatabaseInitializer}
import com.example.wishlist.service.{WishlistService, WishlistServiceJdbcImpl}
import com.typesafe.config.{Config, ConfigFactory}

import javax.sql.DataSource
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object Main {

  def main(args: Array[String]): Unit = {

    val config                               = ConfigFactory.load()
    val dataSourceBuilder: DataSourceBuilder = new HikariCPDataSourceBuilder
    val dataSource: DataSource               = dataSourceBuilder.configureDataSource(config)
    val wishlistService: WishlistService     = new WishlistServiceJdbcImpl(dataSource)
    val route: Route                         = new WishlistController(wishlistService).route

    val dbInitializer: DatabaseInitializer = new SqliteDatabaseInitializer
    dbInitializer.createTablesIfDoNotExist(dataSource, config)

    startHttpServer(route, config)
  }

  private def startHttpServer(route: Route, config: Config): Unit = {
    val serverHost = config.getString("server.host")
    val serverPort = config.getInt("server.port")

    implicit val system: ActorSystem[_]             = ActorSystem(Behaviors.empty, "wishlist-system")
    implicit val executionContext: ExecutionContext = system.executionContext

    val bindingFuture = Http().newServerAt(serverHost, serverPort).bind(route)
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
        .onComplete { _ => system.terminate() }(executionContext)
    }
    // Block until a shutdown signal is received
    Await.result(system.whenTerminated, Duration.Inf)
  }

}
