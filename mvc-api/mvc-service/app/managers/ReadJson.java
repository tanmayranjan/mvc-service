package managers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ReadJson {
    Logger logger = LoggerFactory.getLogger(ReadJson.class);
    String sourceurl = "";
    public void read(String json,boolean flagformvc) {
        try {
            Map<String,Object> contentobj;
            Map<String,Object> obj = JsonUtils.deserialize(json,Map.class);
            Map<String,Object> req = (HashMap<String,Object>) obj.get("request");
            ArrayList<Object> contentarr = (ArrayList<Object>) req.get("content");
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
                    if(GetContentMetadata.validateSourceURL(sourceurl)) {
                        // get content definition
                        GetContentMetadata.getMetadata(contentobj,sourceurl,flagformvc);

                    }
            }
          }
        }
        catch (Exception e) {
            logger.info("ReadJson :: reading json ::: Exception is " + e + " sourceurl is " + sourceurl);
        }

    }

}
