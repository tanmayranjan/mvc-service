package managers;
import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ReadCSV {
    public List<String[]> readCSV(File file) {
        List<String[]> allRows = null;
        try {
            //Build reader instance
            CSVReader reader = new CSVReader(new FileReader(file));

            //Read all rows at once
             allRows = reader.readAll();
        }
        catch (Exception e) {
            System.out.println("Exception in reading CSV " + e);
        }
        return allRows;
    }
}
