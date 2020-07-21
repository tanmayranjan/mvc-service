package managers;

import org.json.simple.JSONObject;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.LinkedHashMap;
import java.util.UUID;

public class EventObjectProducer {
    public static void addToEventObj(JSONObject content, String contentId) throws Exception  {

            JSONObject eventObj = JsonUtils.deserialize(SearchConstants.autocreatejobevent, JSONObject.class);
            final long timeinmillisecond = System.currentTimeMillis();
            eventObj.put("ets", timeinmillisecond);
            UUID uniqueKey = UUID.randomUUID();
            String mid = "VD." + timeinmillisecond + "." + uniqueKey;
            eventObj.put("mid", mid);
            LinkedHashMap<String,Object> object = (LinkedHashMap<String,Object>) eventObj.get("object");
            object.put("id", contentId);
            LinkedHashMap<String,Object> context = (LinkedHashMap<String,Object>) eventObj.get("context");
            context.put("channel", content.get("channel").toString());
            LinkedHashMap<String,Object> edata = (LinkedHashMap<String,Object>) eventObj.get("edata");
            edata.put("repository", SearchConstants.vidyadaanurl + contentId);
            edata.put("metadata", content);
            EventProducer.writeToKafka(eventObj.toString(), SearchConstants.mvctopic);

    }
}
