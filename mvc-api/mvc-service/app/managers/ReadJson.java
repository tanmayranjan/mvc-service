package managers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sunbird.search.util.SearchConstants;
import org.sunbird.kafka.client.KafkaClient;
public class ReadJson {
    String response = null;
    ReadExcel readExcelobj = new ReadExcel();
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
                else if(contentobj.get("sourceurl") != null) {
                    contenturlarr = (JSONArray) contentobj.get("sourceurl");
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
                        response = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                        JSONObject respobj = (JSONObject) parser.parse(response);
                        JSONObject result = (JSONObject) respobj.get("result");
                        JSONObject content = (JSONObject) result.get("content");
                        content.putAll(contentobj);
                        readExcelobj.addToEventObj(content, contentId);
                    }
                }

            }
        }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }


}
