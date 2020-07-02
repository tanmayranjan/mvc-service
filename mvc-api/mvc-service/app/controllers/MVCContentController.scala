package controllers
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.google.inject.Inject
import managers.{ReadExcel, ReadJson}
import org.sunbird.common.dto.ResponseHandler
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

class MVCContentController @Inject()(cc: ControllerComponents) (implicit exec: ExecutionContext) extends SearchBaseController(cc) {
  val mgr: ReadJson = new ReadJson()
  val readexcel: ReadExcel = new ReadExcel()
  def create() = Action.async { implicit request =>
    val body = request.body.asJson.getOrElse("{}").toString
    @transient val mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    if(!body.equals("{}")) {
     Future {
       mgr.read(body)
     }
    }
    else {
      val file = request.body.asMultipartFormData.get.file("File").get.ref
      Future {
        readexcel.readfile(file)
      }
    }
    val result = ResponseHandler.OK()
    val response = mapper.writeValueAsString(result);
    Future(Ok(response).as("application/json"))
  }

}
