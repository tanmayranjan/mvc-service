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
               content.remove("Textbook Name");
           }
            if(content.get("textbook name") != null) {
                content.put("textbookName",content.get("textbook name"));
                content.remove("textbook name");
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
            writeToKafka(eventObj.toString());

        }
        catch (Exception e) {

        }
    }

    private static JSONObject modifyconceptfields(JSONObject content) {
        if(content.get("Chapter Concept Name") != null) {
           content =  putandremove(content , "Chapter Concept Name" , "level1Concept");
        }
        else if(content.get("chapter concept name") != null) {
            content =  putandremove(content , "chapter concept name" , "level1Concept");
        }
        if(content.get("Topic Concept Name") != null) {
            content =  putandremove(content , "Topic Concept Name" , "level2Concept");
        }
        else if(content.get("topic concept name") != null) {
            content =  putandremove(content , "topic concept name" , "level2Concept");
        }

        if(content.get("Sub Topic Concept Name") != null) {
            content =  putandremove(content , "Sub Topic Concept Name" , "level3Concept");
        }
        else if(content.get("Sub Topic Concept Name") != null) {
            content =  putandremove(content , "sub topic concept name" , "level3Concept");
        }
        if(content.get("Chapter Name") != null) {
            content =  putandremove(content , "Chapter Name" , "level1Name");
        }
        else if(content.get("chapter name") != null) {
            content =  putandremove(content , "chapter name" , "level1Name");
        }
        if(content.get("Topic Name") != null) {
            content =  putandremove(content , "Topic Name" , "level2Name");
        }
        else if(content.get("topic name") != null) {
            content =  putandremove(content , "topic name" , "level2Name");
        }
        if(content.get("sub topic name") != null) {
            content =  putandremove(content , "sub topic name" , "level3Name");
        }
        else if(content.get("sub topic name") != null) {
            content =  putandremove(content , "sub topic name" , "level3Name");
        }
        if(content.get("cid") != null) {
            content.remove("cid");
        }
        return content;
    }

    private static JSONObject putandremove(JSONObject content, String oldkey, String newkey) {
        content.put(newkey,content.get(oldkey));
        content.remove(oldkey);
        return content;
    }

    public static void writeToKafka(String event) {
        try {
            new KafkaClient().send(event,SearchConstants.mvctopic);

        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
