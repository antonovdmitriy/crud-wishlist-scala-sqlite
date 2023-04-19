import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class MainSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "Main" should {

    "start the app" in {

      val f = Future {
        Main.main(Array.empty)
      }

      val config = ConfigFactory.load("application-test.conf")

      val request = HttpRequest(
        GET,
        uri =
          Uri(s"http://${config.getString("server.host")}:${config.getInt("server.port")}/health")
      )
      val responseFuture = Http().singleRequest(request)
      val response       = Await.result(responseFuture, 5.seconds)

      response.status shouldBe OK
    }
  }
}
