package managers;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sunbird.search.util.SearchConstants;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Postman {
    private static HttpClient client;

    public static HttpClient getHttpClient() {
        if (client == null) {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300 * 1000).setSocketTimeout(300 * 1000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        }
        return client;
    }
public static Map<String,Object> setVectorList(Map<String,Object> filters) {
        try {


            Map<String, Object> originalFIlter = (Map<String, Object>) filters.get("filters");
            String ml_level1 = "", ml_level2 = "", ml_level3 = "";
            Set<Double> ml_contentTextVector = null;
            if (originalFIlter.get("ml_level1Concept") != null) {
                ml_level1 = originalFIlter.get("ml_level1Concept").toString();
                originalFIlter.remove("ml_level1Concept");
            }
            if (originalFIlter.get("ml_level2Concept") != null) {
                ml_level2 = originalFIlter.get("ml_level2Concept").toString();
                originalFIlter.remove("ml_level2Concept");
            }
            if (originalFIlter.get("ml_level3Concept") != null) {
                ml_level3 = originalFIlter.get("ml_level3Concept").toString();
                originalFIlter.remove("ml_level3Concept");
            }
          String text =  ml_level1 + " " + ml_level2 + " " + ml_level3;
           text = text.trim();
                JSONObject obj = new JSONObject(SearchConstants.mlvectorListRequest);
                JSONObject req = ((JSONObject) (obj.get("request")));
                JSONArray text1 = (JSONArray) req.get("text");
                text1.put(text);
                JSONObject respobj = new JSONObject(POST(obj.toString(), SearchConstants.mlvectorurl));
                JSONObject result = (JSONObject) respobj.get("result");
                JSONArray ml_contentTextVectorList = result.get("vector") != null ? (JSONArray) result.get("vector") : null;
                if (ml_contentTextVectorList != null) {
                    ml_contentTextVectorList = (JSONArray) ml_contentTextVectorList.get(0);
                    ArrayList<Double> vector= new ArrayList<Double>();
                    for(int i = 0 ; i < ml_contentTextVectorList.length() ; i++) {
                        vector.add(ml_contentTextVectorList.getDouble(i));
                    }
                    originalFIlter.put("ml_contentTextVector", vector);
                }

        }
        catch (Exception e) {
            System.out.println(e);
        }
    return filters;
}
    public static Map<String, Object> convertWithStream(String mapAsString) {
        Map<String, Object> map = Arrays.stream(mapAsString.split(","))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        return map;
    }
    public static String POST(String requestbody,String url) {
        @SuppressWarnings("deprecation")
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(requestbody, "UTF8");
        String strResponse =  null;
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("response::"+response);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output=null;
            while ((output = br.readLine()) != null) {
                strResponse = output;
            }
            return  strResponse;
        } catch (ClientProtocolException e) {
            System.out.println(e);
            return null;
        } catch (IOException e) {
            System.out.println(e);
            return  null;
        }
    }
}