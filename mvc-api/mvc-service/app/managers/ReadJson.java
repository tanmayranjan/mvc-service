package managers;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadJson {
    public void read(String json) {
        try {
            Map<String,Object> contentobj;
            Map<String,Object> obj = JsonUtils.deserialize(json,Map.class);
            Map<String,Object> req = (LinkedHashMap<String,Object>) obj.get("request");
            ArrayList<Object> contentarr = (ArrayList<Object>) req.get("content");
            String sourceurl;
            int contentarrlen = contentarr.size() > SearchConstants.contentArrayLimit ? SearchConstants.contentArrayLimit : contentarr.size();
            for (int j = 0; j < contentarrlen; j++) {
                contentobj = (Map<String,Object>) contentarr.get(j);
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
