package managers;

import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetContentDefinition {
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
    public static void getDefinition(Map<String,Object> contentobj, String sourceurl) {
        String resp = "", contentId = "";
        try {
                contentId = sourceurl.substring(sourceurl.lastIndexOf('/') + 1);
                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                Map<String,Object> respobj = JsonUtils.deserialize(resp,Map.class);
                LinkedHashMap<String,Object> result = (LinkedHashMap<String,Object>) respobj.get("result");

                LinkedHashMap<String,Object> content = (LinkedHashMap<String,Object>) result.get("content");
                Map<String,Object> newobj = new HashMap<String,Object>(content);
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
        Map<String,Object> failedObj = new HashMap<String,Object>();
        failedObj.put("sourceURL", sourceurl);
        EventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
    }
   }
