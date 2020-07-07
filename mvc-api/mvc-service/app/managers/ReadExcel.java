package managers;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sunbird.kafka.client.KafkaClient;
import org.sunbird.search.util.SearchConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class ReadExcel {
    KafkaClient kafkaclientobj = new KafkaClient();
    public void readfile(File inputExcelFile) {
        try {
            String resp = "" ,respcode = "" , contenturl = "",key,contentId = "";
            JSONObject contentobj = new JSONObject();
            JSONParser parser = new JSONParser();
            JSONObject content = new JSONObject();
            InputStream is = new FileInputStream(inputExcelFile);

            // Get the workbook instance for XLSX/XLS file
            XSSFWorkbook wb = new XSSFWorkbook(is);

            int iNumOfSheets = wb.getNumberOfSheets();
            for (int iIndex = 0; iIndex < iNumOfSheets; iIndex++) {

                // Get sheet from the workbook
                XSSFSheet sheet = wb.getSheetAt(iIndex);

                XSSFRow row;
                XSSFRow sheetheading = sheet.getRow(0);
                XSSFCell cell;
                int numberofrows = sheet.getPhysicalNumberOfRows();
                for (int i = 1; i < numberofrows; i++) {

                    row = sheet.getRow(i);
                    if (row == null) {
                        // If there is an empty row skip it
                        continue;
                    }
                    int numberofColumns = row.getLastCellNum();
                    for (int j = 0; j < numberofColumns; j++) {
                        cell = row.getCell(j);
                        if (cell == null || cell.toString().isBlank() || cell.toString().isEmpty()) {
                            // if last column is null
                            if(j == numberofColumns - 1 && respcode.equals("200")) {
                                content.putAll(contentobj);
                                createEventObj(content, contentId);
                            }
                            continue;
                        }
                        // get the column heading
                        key = sheetheading.getCell(j).toString().toLowerCase();
                        if(key.equalsIgnoreCase("grade")) {
                            // content schema uses gradeLevel not grade as key
                            key = "gradeLevel";
                            contentobj.put(key,splitString(cell.toString()));
                        }
                        else if(key.equalsIgnoreCase("board")){
                            // board stores a string always
                            contentobj.put(key,cell.toString());
                        }
                        else if(key.equalsIgnoreCase("content url")) {
                            key = "sourceURL";
                            contenturl = cell.toString();

                            // Remove new lines and tabs
                            contenturl = contenturl.replaceAll("[\\n\\t ]", "");
                            if(cell.toString().contains("?mode=edit")){
                                contenturl = cell.toString().substring(0,cell.toString().lastIndexOf('?'));
                            }
                            contentobj.put(key,contenturl);
                            respcode = Postman.GET(contenturl).get("statuscode").toString();
                            // check if content url is valid or not
                            if(respcode.equals("200")) {
                                contentId = contenturl.substring(contenturl.lastIndexOf('/') + 1);
                                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                                JSONObject respobj = (JSONObject) parser.parse(resp);
                                JSONObject result = (JSONObject) respobj.get("result");
                                content = (JSONObject) result.get("content");

                            }
                            else {
                                JSONObject failedObj = new JSONObject();
                                failedObj.put("sourceURL",contenturl);
                                writeToKafka(failedObj.toString(),SearchConstants.mvcFailedtopic);
                            }
                        }
                        else  {
                            contentobj.put(key,splitString(cell.toString()));
                        }
                        // when all columns have been iterated
                        if(j == numberofColumns - 1 && respcode.equals("200")) {
                            content.putAll(contentobj);
                            createEventObj(content, contentId);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public JSONArray splitString(String str) {
        String a[] = str.split("\\s*,\\s*");
        JSONArray  strarr = new JSONArray();
        strarr.addAll(Arrays.asList(a));
        return strarr;
    }

    public void createEventObj(JSONObject content,String contentId) {
        try {
            content = modifyconceptfields(content);
            if(content.get("textbook name") != null) {
                content.put("textbook_name",content.get("textbook name"));
                content.remove("textbook name");
            }

            addToEventObj(content, contentId);

        }
        catch (Exception e) {

        }
    }

    public void addToEventObj(JSONObject content, String contentId) throws ParseException {
        // Add label
        content.put("label","MVC");
        JSONParser event = new JSONParser();
        JSONObject eventObj = (JSONObject)event.parse(SearchConstants.autocreatejobevent);
        JSONObject object = (JSONObject)eventObj.get("object");
        object.put("id",contentId);
        JSONObject edata = (JSONObject)eventObj.get("edata");
        edata.put("repository",SearchConstants.vidyadaanurl + contentId);
        edata.put("metadata",content);
        writeToKafka(eventObj.toString(),SearchConstants.mvctopic);
    }

    private  JSONObject modifyconceptfields(JSONObject content) {
        if(content.get("chapter concept name") != null) {
            content =  addnewkey(content , "chapter concept name" , "level1Concept");
        }
        if(content.get("topic concept name") != null) {
            content =  addnewkey(content , "topic concept name" , "level2Concept");
        }

        if(content.get("Sub Topic Concept Name") != null) {
            content =  addnewkey(content , "sub topic concept name" , "level3Concept");
        }
        if(content.get("chapter name") != null) {
            content =  addnewkey(content , "chapter name" , "level1Name");
        }
        if(content.get("topic name") != null) {
            content =  addnewkey(content , "topic name" , "level2Name");
         }
        if(content.get("sub topic name") != null) {
            content =  addnewkey(content , "sub topic name" , "level3Name");
        }
        if(content.get("cid") != null) {
            content.remove("cid");
        }
        return content;
    }

    private  JSONObject addnewkey(JSONObject content, String oldkey, String newkey) {
        content.put(newkey,content.get(oldkey));
        content.remove(oldkey);
        return content;
    }

    public  void writeToKafka(String event,String topic) {
        try {
            kafkaclientobj.send(event,topic);

        }
        catch (Exception e) {
            System.out.println("Exception while writing to kafka topic" + e);
        }
    }
}
