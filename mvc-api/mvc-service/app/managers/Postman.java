package managers;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Postman {
    private static HttpClient client;

    public static HttpClient getHttpClient() {
        if (client == null) {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300 * 1000).setSocketTimeout(300 * 1000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        }
        return client;
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
    public static JSONObject GET(String url)throws Exception {
        HttpGet get = new HttpGet(url);
        String strResponse = null;
        get.setHeader("Content-Type", "application/json; charset=utf-8");
        HttpResponse response = getHttpClient().execute(get);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        int statuscode = response.getStatusLine().getStatusCode();
        JSONObject resp = new JSONObject();
        resp.put("statuscode",statuscode);
        String line;

        while ((line = reader.readLine()) != null) {
            strResponse = line;
        }
        reader.close();

        if(strResponse==null || strResponse.isEmpty())
            strResponse = response.getStatusLine().getReasonPhrase();
        resp.put("response",strResponse);
        return resp;
    }
}