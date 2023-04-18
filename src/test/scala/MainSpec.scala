import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.{Await, Future}

class MainSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "Main" should {

    "start the app" in {

      val f = Future {
        Main.main(Array.empty)
      }

      // Wait for some time for the server to start up
      Thread.sleep(1000)

      // Send a GET request to /health endpoint
      val request = HttpRequest(GET, uri = Uri("http://127.0.0.1:8080/health"))
      val responseFuture = Http().singleRequest(request)

      // Await the response and check the status code
      val response = Await.result(responseFuture, 3.seconds)
      response.status shouldBe OK
    }

  }

}
