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

      val request = HttpRequest(GET, uri = Uri("http://127.0.0.1:3000/health"))
      val responseFuture = Http().singleRequest(request)

      val response = Await.result(responseFuture, 5.seconds)
      response.status shouldBe OK
    }
  }
}
