package managers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sunbird.search.util.SearchConstants;

public class ReadJson {
    GetContentDefinition getContentDefinition = new GetContentDefinition();
    public void read(String json) {
        ObjectMapper mapper =new  ObjectMapper();
        try {
            JSONObject contentobj;
            JSONObject obj = mapper.readValue(json,JSONObject.class);
            JSONObject req = (JSONObject) obj.get("request");
            JSONArray contentarr = (JSONArray) req.get("content");
            String sourceurl;
            int contentarrlen = contentarr.size() > SearchConstants.contentArrayLimit ? SearchConstants.contentArrayLimit : contentarr.size();
            for (int j = 0; j < contentarrlen; j++) {
                contentobj = (JSONObject) contentarr.get(j);
                if(contentobj.get("sourceURL") != null) {
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
