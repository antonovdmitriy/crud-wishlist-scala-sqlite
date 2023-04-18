import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.concurrent.{Await, Future}

class MainSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "Main" should {

    "start the app" in {

      val f = Future {
        Main.main(Array.empty)
      }

      // Wait for some time for the server to start up

      println(
        "before sleep " + Instant.now() + " " + Thread.currentThread().getId
      )

      Thread.sleep(5000)

      println(
        "after sleep " + Instant.now() + " " + Thread.currentThread().getId
      )

      println(
        "before http request from test " + Instant
          .now() + " " + Thread.currentThread().getId
      )

      // Send a GET request to /health endpoint
      val request = HttpRequest(GET, uri = Uri("http://127.0.0.1:3000/health"))
      val responseFuture = Http().singleRequest(request)

      // Await the response and check the status code
      val response = Await.result(responseFuture, 5.seconds)

      println(
        "after http request from test " + Instant
          .now() + " " + Thread.currentThread().getId
      )

      response.status shouldBe OK

    }

  }

}
