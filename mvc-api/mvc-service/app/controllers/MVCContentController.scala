package controllers
import java.nio.file.Paths

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.google.inject.Inject
import managers.{CSVContentProcessor, ReadCSV, ReadJson}
import org.sunbird.common.dto.ResponseHandler
import org.sunbird.common.exception.ResponseCode
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

class MVCContentController @Inject()(cc: ControllerComponents) (implicit exec: ExecutionContext) extends SearchBaseController(cc) {
  val readJson: ReadJson = new ReadJson()
  val processCSV: CSVContentProcessor = new CSVContentProcessor()
  val readCSV: ReadCSV = new ReadCSV();
  @transient val mapper = new ObjectMapper();
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  def createUsingCSV() = Action.async { implicit request =>
    val body = request.body.asJson.getOrElse("{}").toString
    var result = ResponseHandler.OK()
    if(body.equals("{}")) {
      val multipart = request.body.asMultipartFormData
      val file = multipart.get.file("File").get.ref;
      Future {
        val rows = readCSV.readCSV(file)
        processCSV.processCSVRows(rows)
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
    if(!body.equals("{}")) {
      Future {
        readJson.read(body)
      }
    }
    else {
      result = ResponseHandler.ERROR(ResponseCode.CLIENT_ERROR,"400","Bad Request,Please provide valid input")
    }
    val response = mapper.writeValueAsString(result);
    Future(Ok(response).as("application/json"))
  }

}
