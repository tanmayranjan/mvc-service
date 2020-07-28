package controllers

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.google.inject.Inject
import managers.ReadJson
import org.sunbird.common.dto.ResponseHandler
import org.sunbird.common.exception.ResponseCode
import play.api.mvc.ControllerComponents
import java.util.UUID.randomUUID
import scala.concurrent.{ExecutionContext, Future}
import org.joda.time.{DateTime, DateTimeZone}
import scala.collection.JavaConverters._

class MVCContentController @Inject()(cc: ControllerComponents) (implicit exec: ExecutionContext) extends SearchBaseController(cc) {
  val readJson: ReadJson = new ReadJson()
  @transient val mapper = new ObjectMapper();
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  def createUsingJson() = Action.async { implicit request =>
    val body = request.body.asJson.getOrElse("{}").toString
    val id = randomUUID().toString
    val jmap = new java.util.HashMap[String, Object]()
    jmap.put("id", id)
    var result = ResponseHandler.OK()
    if(!body.equals("{}")) {
      Future {
       readJson.read(body)
      }
      result.setTs((DateTime.now(DateTimeZone.UTC).getMillis().toString))
      result.setId(id)
      result.putAll(jmap)
      val response = mapper.writeValueAsString(result);
      Future(Ok(response).as("application/json"))
    }
    else {
      result = ResponseHandler.ERROR(ResponseCode.CLIENT_ERROR,"400","Bad Request,Please provide valid input")
      result.setTs((DateTime.now(DateTimeZone.UTC).getMillis().toString))
      result.setId(id)
      result.putAll(jmap)
      val response = mapper.writeValueAsString(result);
      Future(BadRequest(response).as("application/json"))
    }
  }
}
