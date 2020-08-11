package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, defaultAwaitTimeout, status,BAD_REQUEST}

class MVCContentSpec extends BaseSpec {
  import play.api.libs.json._

  val json: JsValue = Json.parse("""
  {
  "request": {
    "content": [
      {
        "board": "CBSE",
        "subject": [
          "Science"
        ],
        "medium": [
          "English"
        ],
        "gradeLevel": [
          "Class 6"
        ],
        "textbook_name": [
          "Science"
        ],
        "level1Name": [
          "Getting To Know Plants"
        ],
        "level1Concept": [
          "Plants"
        ],
        "source": "TN 1.1",
        "sourceURL": "https://diksha.gov.in/play/content/do_31251475641667584025534"
      }
    ]
  }
}
  """)

  "MVCContent" should {
    "return 200 if request is valid" in {
      val controller = app.injector.instanceOf[controllers.MVCContentController]
      val response = controller.createUsingJson(None)(FakeRequest().withJsonBody(json))
      isOK(response)
      status(response)must equalTo(OK)
    }

    "return 400 if request is valid" in {
      val controller = app.injector.instanceOf[controllers.MVCContentController]
      val response = controller.createUsingJson(None)(FakeRequest())
      hasClientError(response)
      status(response) must equalTo(400)
    }
  }
}
