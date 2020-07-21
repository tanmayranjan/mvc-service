package managers;

import org.json.simple.JSONObject;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.LinkedHashMap;

public class GetContentDefinition {
    EventProducer eventProducer = new EventProducer();
    public static boolean validateSourceURL(String sourceurl) {
        String respcode = "";
        try {
            respcode = Postman.GET(sourceurl).get("statuscode").toString();
            // check if source url is valid or not
            if (respcode.equals("200")) {
                return true;
            }
        }
        catch (Exception e) {
            insertintoFailedEventTopic(sourceurl);
        }

        return false;
    }
    public static void getDefinition(JSONObject contentobj,String sourceurl) {
        String resp = "", contentId = "";
        try {
                contentId = sourceurl.substring(sourceurl.lastIndexOf('/') + 1);
                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                JSONObject respobj = JsonUtils.deserialize(resp,JSONObject.class);
                LinkedHashMap<String,Object> result = (LinkedHashMap<String,Object>) respobj.get("result");

                LinkedHashMap<String,Object> content = (LinkedHashMap<String,Object>) result.get("content");
                JSONObject newobj = new JSONObject(content);
                // club metadata of diksha and from csv/json
                newobj.putAll(contentobj);
               EventObjectProducer.addToEventObj(newobj, contentId);

        }
        catch(Exception e)
        {
        System.out.println(e);
            insertintoFailedEventTopic(sourceurl);
        }
     }
  public  static void insertintoFailedEventTopic(String sourceurl){
        JSONObject failedObj = new JSONObject();
        failedObj.put("sourceURL", sourceurl);
        EventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
    }
   }
