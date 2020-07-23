package managers;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Postman {
    private static HttpClient client;

    public static HttpClient getHttpClient() {
        if (client == null) {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300 * 1000).setSocketTimeout(300 * 1000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        }
        return client;
    }
    public static Map<String,Object> GET(String url)throws Exception {
        HttpGet get = new HttpGet(url);
        String strResponse = null;
        get.setHeader("Content-Type", "application/json; charset=utf-8");
        HttpResponse response = getHttpClient().execute(get);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        int statuscode = response.getStatusLine().getStatusCode();
        Map<String,Object> resp = new HashMap<String,Object>();
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