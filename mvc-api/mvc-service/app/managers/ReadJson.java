package managers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sunbird.search.util.SearchConstants;
import org.sunbird.kafka.client.KafkaClient;
public class ReadJson {
    String response = null;
    public void read(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject contentobj;
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONObject req = (JSONObject) obj.get("request");
            JSONArray contentarr = (JSONArray) req.get("content");
            String contentId = "";
            JSONArray contenturlarr;
            for (int j = 0; j < contentarr.size(); j++) {
                contentobj = (JSONObject) contentarr.get(j);
                if(contentobj.get("Content URL") != null) {
                    contenturlarr = (JSONArray) contentobj.get("Content URL");
                    contentobj.put("sourceurl",contenturlarr);
                    contentobj.remove("Content URL");
                }
                else {
                    contenturlarr = null;
                }
                if (contenturlarr != null) {
                String temp = "";
                for (int i = 0; i < contenturlarr.size(); i++) {
                    String contenturl = contenturlarr.get(i).toString();
                    if (temp.equalsIgnoreCase(contenturl)) {
                        continue;
                    }
                    temp = contenturl;
                    response = (Postman.GET(contenturl)).get("statuscode").toString();
                    if (response.equals("200")) {
                        contentId = contenturl.substring(contenturl.lastIndexOf('/') + 1);
                        response = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId + SearchConstants.extraparams).get("response").toString();
                        JSONObject respobj = (JSONObject) parser.parse(response);
                        JSONObject result = (JSONObject) respobj.get("result");
                        JSONObject content = (JSONObject) result.get("content");
                        content.putAll(contentobj);
                        createEventObj(content, contentId);
                    }
                }

            }
        }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

   public static void createEventObj(JSONObject content,String contentId) {
        try {
           content = modifyconceptfields(content);
           if(content.get("Textbook Name") != null) {
               content.put("textbookName",content.get("Textbook Name"));
           }
            // Add label
            content.put("label","MVC");

            JSONParser event = new JSONParser();
            JSONObject eventObj = (JSONObject)event.parse(SearchConstants.autocreatejobevent);
            JSONObject object = (JSONObject)eventObj.get("object");
            object.put("id",contentId);
            JSONObject edata = (JSONObject)eventObj.get("edata");
            edata.put("repository",SearchConstants.vidyadaanurl + contentId);
            edata.put("metadata",content);
            writetoKafka(eventObj.toString());

        }
        catch (Exception e) {

        }
    }

    private static JSONObject modifyconceptfields(JSONObject content) {
        if(content.get("Chapter Concept Name") != null) {
            content.put("ml_level1Concept",content.get("Chapter Concept Name"));
        }
        content.remove("Chapter Concept Name");
        if(content.get("Topic Concept Name") != null) {
            content.put("ml_level2Concept",content.get("Topic Concept Name"));
        }
        content.remove("Topic Concept Name");

        if(content.get("Sub Topic Concept Name") != null) {
            content.put("ml_level3Concept",content.get("Sub Topic Concept Name"));
        }
        content.remove("Sub Topic Concept Name");
        if(content.get("Chapter Name") != null) {
            content.put("level1ConceptName",content.get("Chapter Name"));
        }
        content.remove("Chapter Name");
        if(content.get("Topic Name") != null) {
            content.put("level2ConceptName",content.get("Topic Name"));
        }
        content.remove("Topic Name");

        if(content.get("Sub Topic Name") != null) {
            content.put("level3ConceptName",content.get("Sub Topic Name"));
        }
        content.remove("Sub Topic Name");
        return content;
    }

    public static void writetoKafka(String event) {
        try {
            new KafkaClient().send(event,SearchConstants.mvctopic);

        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
