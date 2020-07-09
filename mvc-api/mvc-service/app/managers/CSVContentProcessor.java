package managers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sunbird.search.util.SearchConstants;

import java.io.*;
import java.util.Arrays;

public class CSVContentProcessor {
GetContentDefinition getContentDefinition = new GetContentDefinition();
    JSONObject dataKeys;
    JSONParser parser = new JSONParser();
    public void readCsvFile(File csvFile) {
        String[] header = {};
        int rowCount = 0;

        try {
            dataKeys = (JSONObject) parser.parse(SearchConstants.contentKeysObj);
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (rowCount == 0) {
                        header = line.split(SearchConstants.csvfiledelimeter);
                    } else {
                        String[] values = line.split(SearchConstants.csvfiledelimeter);
                        JSONObject data = getJsonObjectForValues(header, values);
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
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
        catch (Exception e) {

        }

    }
    public JSONArray splitString(String str) {
        String a[] = str.split("\\"+SearchConstants.csvcolumndelimeter+"\\s*");
        JSONArray  strarr = new JSONArray();
        strarr.addAll(Arrays.asList(a));
        return strarr;
    }





// Create JSON object from CSV keys and values
    public JSONObject getJsonObjectForValues(String[] keys, String[] value) {
        JSONObject data = new JSONObject();
        try {
            String dataKey="" , dataValue , header;
            for(int pos=0; pos < value.length; pos++) {
                header = keys[pos];
                if(header != "" && header != null) {
                    header = header.toLowerCase().replaceAll("\\s", "");
                    dataKey =dataKeys.containsKey(header) ? dataKeys.get(header).toString() : "";

                }

                dataValue = value[pos];
                if(!dataKey.equals("") && !dataValue.equals("")) {
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
