package managers;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sunbird.search.util.SearchConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class ReadExcel {
    public void readfile(File inputExcelFile) {
        try {
            String resp = "" , contenturl = "",key,contentId;
            JSONObject contentobj = null;
            JSONParser parser = new JSONParser();
            String strFileExtn = (inputExcelFile.getName().substring(inputExcelFile.getName().lastIndexOf(".") + 1));
            InputStream is = new FileInputStream(inputExcelFile);

            // Get the workbook instance for XLSX/XLS file
            XSSFWorkbook wb = new XSSFWorkbook(is);

            int iNumOfSheets = wb.getNumberOfSheets();
            for (int iIndex = 0; iIndex < iNumOfSheets; iIndex++) {
                int iRow = 0;
                // Get first sheet from the workbook
                XSSFSheet sheet = wb.getSheetAt(iIndex);

                XSSFRow row;
                XSSFRow sheetheading = sheet.getRow(0);
                XSSFCell cell;
                XSSFCell termCell;
                XSSFCell tempName;
                XSSFCell header;
                int numberofrows = sheet.getPhysicalNumberOfRows();
                int numberofrows2 = sheet.getLastRowNum() + 1;
                for (int i = 1; i < numberofrows; i++) {

                    row = sheet.getRow(i);
                    if (row == null) {
                        //do something with an empty row
                        continue;
                    }
                    int numberofColumns = row.getLastCellNum();
                    for (int j = 0; j < numberofColumns; j++) {
                        cell = row.getCell(j);
                        if (cell == null) {
                            continue;
                        }
                        key = sheetheading.getCell(j).toString().toLowerCase();
                        if(key.equalsIgnoreCase("grade")) {
                            key = "gradeLevel";
                            contentobj.put(key,splitString(cell.toString()));
                        }
                        else  {
                            contentobj.put(key,splitString(cell.toString()));
                        }
                        if(key.equalsIgnoreCase("content url")) {
                            key = "sourceurl";

                            contenturl = cell.toString();
                            contenturl = contenturl.replaceAll("[\\n\\t ]", "");
                            if(cell.toString().contains("?mode=edit")){

                                contenturl = cell.toString().substring(0,cell.toString().lastIndexOf('?'));
                            }
                            contentobj.put(key,contenturl);
                           resp = Postman.GET(contenturl).get("statuscode").toString();
                           if(resp.equals("200")) {
                               contentId = contenturl.substring(contenturl.lastIndexOf('/') + 1);
                               resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId + SearchConstants.extraparams).get("response").toString();
                               JSONObject respobj = (JSONObject) parser.parse(resp);
                               JSONObject result = (JSONObject) respobj.get("result");
                               JSONObject content = (JSONObject) result.get("content");
                               content.putAll(contentobj);
                               ReadJson.createEventObj(content, contentId);
                           }
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
        JSONArray  strarr= (JSONArray) Arrays.asList(str.split("\\s*,\\s*"));
        return strarr;
    }
}
