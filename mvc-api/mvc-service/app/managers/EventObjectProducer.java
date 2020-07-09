package managers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sunbird.search.util.SearchConstants;

import java.util.UUID;

public class EventObjectProducer {
    EventProducer eventProducer = new EventProducer();
    public void addToEventObj(JSONObject content, String contentId) throws ParseException {
        // Add label
        content.put("label","MVC");
        JSONParser event = new JSONParser();
        JSONObject eventObj = (JSONObject)event.parse(SearchConstants.autocreatejobevent);
        final long timeinmillisecond = System.currentTimeMillis();
        eventObj.put("ets",timeinmillisecond);
        UUID uniqueKey = UUID.randomUUID();
        String mid = "VD."+ timeinmillisecond + "." + uniqueKey;
        eventObj.put("mid",mid);
        JSONObject object = (JSONObject)eventObj.get("object");
        object.put("id",contentId);
        JSONObject context = (JSONObject)eventObj.get("context");
        context.put("channel",content.get("channel").toString());
        JSONObject edata = (JSONObject)eventObj.get("edata");
        edata.put("repository",SearchConstants.vidyadaanurl + contentId);
        edata.put("metadata",content);
        eventProducer.writeToKafka(eventObj.toString(),SearchConstants.mvctopic);
    }
}
