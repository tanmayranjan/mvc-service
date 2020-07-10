package managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sunbird.search.util.SearchConstants;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVContentProcessor {
GetContentDefinition getContentDefinition = new GetContentDefinition();
    Map<String,Object> dataKeys;
    ObjectMapper mapper = new ObjectMapper();
    public void processCSVRows(List<String[]> csvRows) {
        List<String> header ;
        int rowCount = 0;
        int numberofrows;
        try {
            dataKeys = mapper.readValue(SearchConstants.contentKeysObj,Map.class);
            Set<String> allkeys = dataKeys.keySet();
                JSONObject headerobj = new JSONObject();
                if(csvRows.size() > SearchConstants.csvRowsLimit) {
                    numberofrows = SearchConstants.csvRowsLimit;
                    System.out.println("Number of rows are greater than " + SearchConstants.csvRowsLimit);
                }
                else {
                    numberofrows = csvRows.size();
                }
                while (rowCount < numberofrows) {
                    if (rowCount == 0) {
                        header = Arrays.asList(csvRows.get(rowCount));
                        String headerkey;
                        for(int i = 0 ; i < header.size() ; i++) {
                            headerkey = header.get(i).toLowerCase().replaceAll("\\s", "");
                            if(dataKeys.containsKey(headerkey)) {
                                headerobj.put(headerkey,i);
                            }

                        }
                    } else {
                        String[] values = csvRows.get(rowCount);
                        JSONObject data = getJsonObjectForValues(headerobj, values , allkeys);
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
    public JSONObject getJsonObjectForValues(JSONObject keys, String[] value , Set<String> allkeys) {
        JSONObject data = new JSONObject();
        try {
            String dataKey="" , dataValue  , header;
            int headerIndex;

            for (String ele : allkeys ) {
                dataKey = (String) dataKeys.get(ele);
                headerIndex = (int) keys.get(ele);
                dataValue = "";
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
