package controllers
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.google.inject.Inject
import managers.{ReadExcel, ReadJson}
import org.sunbird.common.dto.ResponseHandler
import org.sunbird.common.exception.ResponseCode
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

class MVCContentController @Inject()(cc: ControllerComponents) (implicit exec: ExecutionContext) extends SearchBaseController(cc) {
  val mgr: ReadJson = new ReadJson()
  val readexcel: ReadExcel = new ReadExcel()
  def createUsingExcel() = Action.async { implicit request =>
    val body = request.body.asJson.getOrElse("{}").toString
    var result = ResponseHandler.OK()
    @transient val mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    if(body.equals("{}")) {
      val file = request.body.asMultipartFormData.get.file("File").get.ref
      Future {
        readexcel.readfile(file)
      }
    }
    else {
      result = ResponseHandler.ERROR(ResponseCode.CLIENT_ERROR,"400","Bad Request,Please provide valid input")
    }
    val response = mapper.writeValueAsString(result);
    Future(Ok(response).as("application/json"))
  }
  def createUsingJson() = Action.async { implicit request =>
    val body = request.body.asJson.getOrElse("{}").toString
    var result = ResponseHandler.OK()
    @transient val mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    if(!body.equals("{}")) {
      Future {
        mgr.read(body)
      }
    }
    else {
      result = ResponseHandler.ERROR(ResponseCode.CLIENT_ERROR,"400","Bad Request,Please provide valid input")
    }
    val response = mapper.writeValueAsString(result);
    Future(Ok(response).as("application/json"))
  }

}
