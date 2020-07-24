package managers;

import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.Map;
import java.util.UUID;

public class EventObjectProducer {
    EventProducer eventProducer = new EventProducer();
    public void addToEventObj(Map<String,Object> content, String contentId) throws Exception  {

            Map<String,Object> eventObj = JsonUtils.deserialize(SearchConstants.autocreatejobevent, Map.class);
            final long timeinmillisecond = System.currentTimeMillis();
            eventObj.put("ets", timeinmillisecond);
            UUID uniqueKey = UUID.randomUUID();
            String mid = "VD." + timeinmillisecond + "." + uniqueKey;
            eventObj.put("mid", mid);
            Map<String,Object> object = (Map<String,Object>) eventObj.get("object");
            object.put("id", contentId);
            Map<String,Object> context = (Map<String,Object>) eventObj.get("context");
            context.put("channel", content.get("channel").toString());
            Map<String,Object> edata = (Map<String,Object>) eventObj.get("edata");
            edata.put("repository", SearchConstants.vidyadaanurl + contentId);
            edata.put("metadata", content);
            eventProducer.writeToKafka(JsonUtils.serialize(eventObj), SearchConstants.mvctopic);

    }
}
