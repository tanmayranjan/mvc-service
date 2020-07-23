package managers;

import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetContentDefinition {
    EventObjectProducer eventObjectProducer = new EventObjectProducer();
    EventProducer eventProducer = new EventProducer();
    public void getDefinition(Map<String,Object> contentobj, String sourceurl) throws Exception {
        String resp = "", respcode = "", contentId = "";
        Map<String,Object> failedObj = new HashMap<>();
        try {
            respcode = Postman.GET(sourceurl).get("statuscode").toString();
            // check if source url is valid or not
            if (respcode.equals("200")) {
                contentId = sourceurl.substring(sourceurl.lastIndexOf('/') + 1);
                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                Map<String,Object> respobj = JsonUtils.deserialize(resp,Map.class);
                Map<String,Object> result = (LinkedHashMap<String,Object>) respobj.get("result");

                Map<String,Object> content = (LinkedHashMap<String,Object>) result.get("content");

                // club metadata
                content.putAll(contentobj);
                eventObjectProducer.addToEventObj(content, contentId);
            } else {
                // if source url is not valid , send an event to another topic
                failedObj.put("sourceURL", sourceurl);
                eventProducer.writeToKafka(JsonUtils.serialize(failedObj), SearchConstants.mvcFailedtopic);
            }
        }
        catch(Exception e)
        {
        System.out.println(e);
            failedObj.put("sourceURL", sourceurl);
            eventProducer.writeToKafka(JsonUtils.serialize(failedObj), SearchConstants.mvcFailedtopic);
        }
     }
   }
