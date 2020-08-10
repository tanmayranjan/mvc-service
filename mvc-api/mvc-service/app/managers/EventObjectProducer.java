package managers;

import org.sunbird.common.JsonUtils;
import org.sunbird.common.Platform;
import org.sunbird.search.util.SearchConstants;
import java.util.Map;
import java.util.UUID;

public class EventObjectProducer {
        public static void addToEventObj(Map<String,Object> content, String contentId) throws Exception  {

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
                 if(content.containsKey("concepts")) {
                    content.remove("concepts");
                   }
                 if(content.containsKey("questions")) {
                    content.remove("questions");
                  }
                edata.put("metadata", content);
                EventProducer.writeToKafka(JsonUtils.serialize(eventObj), Platform.config.getString("kafka.topics.instruction"));

        }
}
