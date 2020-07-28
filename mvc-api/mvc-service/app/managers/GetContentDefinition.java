package managers;

import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.HashMap;
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
                Map<String,Object> result = (HashMap<String,Object>) respobj.get("result");

                Map<String,Object> content = (HashMap<String,Object>) result.get("content");
                // club metadata of diksha and from json
                content.putAll(contentobj);
               EventObjectProducer.addToEventObj(content, contentId);

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
