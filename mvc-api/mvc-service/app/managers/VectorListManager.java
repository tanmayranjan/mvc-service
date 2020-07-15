package managers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VectorListManager {
    String text = "";
        public  Map<String,Object> setVectorList(String strRequest) {
        try {

            Map<String, Object> strReq = JsonUtils.deserialize(strRequest, Map.class);
            Map<String,Object> originalFIlter = (Map<String,Object>)strReq.get("filters");
            List<String> level1 = null, level2 = null, level3 = null;
            if (originalFIlter.get("level1Name") != null) {
                level1 = (List<String>)originalFIlter.get("level1Name");
                text =  iterateList(level1);
                  originalFIlter.remove("level1Name");
            }
            if (originalFIlter.get("level2Name") != null) {
                level2 = (List<String>)originalFIlter.get("level2Name");
                text =  iterateList(level2);
                 originalFIlter.remove("level2Name");
            }
            if (originalFIlter.get("level3Name") != null) {
                level3 = (List<String>)originalFIlter.get("level3Name");
                text =  iterateList(level3);
                 originalFIlter.remove("level3Name");
            }
            text = text.trim();
            if(text.equals("")) {
                return null;
            }
            JSONObject obj = new JSONObject(SearchConstants.mlvectorListRequest);
            JSONObject req = ((JSONObject) (obj.get("request")));
            JSONArray text1 = (JSONArray) req.get("text");
            text1.put(text);
            JSONObject respobj = new JSONObject(Postman.POST(obj.toString(), SearchConstants.mlvectorurl));
            JSONObject result = (JSONObject) respobj.get("result");
            JSONArray contentTextVectorList = result.get("vector") != null ? (JSONArray) result.get("vector") : null;
            if (contentTextVectorList != null) {
                contentTextVectorList = (JSONArray) contentTextVectorList.get(0);
                ArrayList<Double> vector= new ArrayList<Double>();
                for(int i = 0 ; i < contentTextVectorList.length() ; i++) {
                    vector.add(contentTextVectorList.getDouble(i));
                }
                strReq.put("query_vector", vector);
            }

            return strReq;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private  String iterateList(List<String> chapterevelNmae) {

        for(int i = 0 ; i < chapterevelNmae.size() ; i++) {
            text = text + chapterevelNmae.get(i) + " ";
        }
        return text;
    }
}
