package managers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

public class ReadJson {
    public void read(String json) {
        try {
            JSONObject contentobj;
            JSONObject obj = JsonUtils.deserialize(json,JSONObject.class);
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
                    if(GetContentDefinition.validateSourceURL(sourceurl)) {
                        // get content definition
                        GetContentDefinition.getDefinition(contentobj,sourceurl);

                    }
            }
          }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }


}
