package managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sunbird.search.util.SearchConstants;
import java.util.Arrays;
import java.util.List;

public class CSVContentProcessor {
GetContentDefinition getContentDefinition = new GetContentDefinition();
    JSONObject dataKeys;
    ObjectMapper mapper = new ObjectMapper();
    public void readCsvFile(List<String[]> csvRows) {
        String[] header = {};
        int rowCount = 0;

        try {
            dataKeys = mapper.readValue(SearchConstants.contentKeysObj,JSONObject.class);

                JSONObject headerobj = new JSONObject();
                while (rowCount < csvRows.size()) {
                    if (rowCount == 0) {
                        header = csvRows.get(rowCount);
                        String headerkey;
                        for(int i = 0 ; i < header.length ; i++) {
                            headerkey = header[i].toLowerCase().replaceAll("\\s", "");
                            if(dataKeys.containsKey(headerkey)) {
                                headerobj.put(header[i],i);
                            }
                        }
                    } else {
                        String[] values = csvRows.get(rowCount);
                        JSONObject data = getJsonObjectForValues(headerobj, values);
                        // Do the processing
                        // get source url
                        String sourceurl = data.get("sourceURL").toString();
                        sourceurl = sourceurl.replaceAll("[\\n\\t ]", "");
                        if (sourceurl.contains("?mode=edit")) {
                            sourceurl = sourceurl.toString().substring(0, sourceurl.toString().lastIndexOf('?'));
                        }
                        // get content definition
                        getContentDefinition.getDefinition(data,sourceurl);
                    }

                    rowCount++;

            }
        }
        catch (Exception e) {
             System.out.println("Exception in read csv" + e);
        }

    }
    public JSONArray splitString(String str) {
        String a[] = str.split("\\"+SearchConstants.csvcolumndelimeter+"\\s*");
        JSONArray  strarr = new JSONArray();
        strarr.addAll(Arrays.asList(a));
        return strarr;
    }





// Create JSON object from CSV keys and values
    public JSONObject getJsonObjectForValues(JSONObject keys, String[] value) {
        JSONObject data = new JSONObject();
        try {
            String dataKey="" , dataValue  , header;
            int headerIndex;

            for(int pos=0; pos < dataKeys.size(); pos++) {
                headerIndex = (int) keys.get(dataKeys.get(pos));
                dataValue = "";
                dataKey = dataKeys.get(pos).toString();
                if( headerIndex <= value.length - 1) {
                    dataValue = value[headerIndex];
                }
                if(!dataValue.equals("")) {
                    if(dataKey.equals("sourceURL") || dataKey.equals("board")) {
                        // Input as a string
                        data.put(dataKey,dataValue);
                    }
                    else {
                        // Input as a array
                        data.put(dataKey,splitString(dataValue));
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return data;
    }

}
