package managers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class ReadJson {
    GetContentDefinition getContentDefinition = new GetContentDefinition();
    public void read(String json) {

        JSONParser parser = new JSONParser();
        try {
            JSONObject contentobj;
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONObject req = (JSONObject) obj.get("request");
            JSONArray contentarr = (JSONArray) req.get("content");
            String sourceurl;
            for (int j = 0; j < contentarr.size(); j++) {
                contentobj = (JSONObject) contentarr.get(j);
                if(contentobj.get("Content URL") != null) {
                    sourceurl =  contentobj.get("Content URL").toString();
                    contentobj.put("sourceURL",sourceurl);
                    contentobj.remove("Content URL");
                }
                else if(contentobj.get("sourceURL") != null) {
                    sourceurl =  contentobj.get("sourceURL").toString();
                }
                else {
                    sourceurl = null;
                }
                if (sourceurl != null) {
                    // get content definition
                    getContentDefinition.getDefinition(contentobj,sourceurl);
            }
          }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }


}
