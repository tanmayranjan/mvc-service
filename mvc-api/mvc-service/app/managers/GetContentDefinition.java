package managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.sunbird.search.util.SearchConstants;

public class GetContentDefinition {
    EventObjectProducer eventObjectProducer = new EventObjectProducer();
    EventProducer eventProducer = new EventProducer();
    ObjectMapper mapper = new ObjectMapper();
    public void getDefinition(JSONObject contentobj,String sourceurl) {
        String resp = "", respcode = "", contentId = "";
        JSONObject content = new JSONObject();
        JSONObject failedObj = new JSONObject();
        try {
            respcode = Postman.GET(sourceurl).get("statuscode").toString();
            // check if source url is valid or not
            if (respcode.equals("200")) {
                contentId = sourceurl.substring(sourceurl.lastIndexOf('/') + 1);
                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                JSONObject respobj = mapper.readValue(resp,JSONObject.class);
                JSONObject result = (JSONObject) respobj.get("result");
                content = (JSONObject) result.get("content");
                // club metadata of diksha and from csv/json
                content.putAll(contentobj);
                eventObjectProducer.addToEventObj(content, contentId);
            } else {
                // if source url is not valid , send an event to another topic
                failedObj.put("sourceURL", sourceurl);
                eventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
            }
        }
        catch(Exception e)
        {
        System.out.println(e);
            failedObj.put("sourceURL", sourceurl);
            eventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
        }
     }
   }
