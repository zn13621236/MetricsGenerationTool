package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import domain.TestMetrcisEntry;

public class GoExecuteJavaDoc {

    public static void main (String[] args) throws IOException {
        String fileName = "fileFolder/GetAllGroupingTryTest2.xls";
        String stepKeyWord = "@Step";
        String verifyKeyWord = "@Verify";
        String preConditionKeyWord = "@PreStep";
        String inputFile = "fileFolder/" + "GetAllGroupingTryTest.java";
        List <TestMetrcisEntry> entryList = parseJavaFile (inputFile, stepKeyWord, verifyKeyWord,preConditionKeyWord);
        writeToExel (entryList, fileName);
    }

    /**
     * @param packageName
     * @param preStep
     * @param keyWord
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static List <TestMetrcisEntry> parseJavaFile (String inputFile, String stepKeyWord, String verifyKeyWord,String preConditionKeyWord) throws FileNotFoundException, IOException {
        // this is to read from java file
        InputStream fis;
        BufferedReader br;
        fis = new FileInputStream (inputFile);
        br = new BufferedReader (new InputStreamReader (fis, Charset.forName ("UTF-8")));
        int count = 0;
        // String result = null;
        String line;
        // init package name it needs to use
        String packageName = null;
        // grep for pre-stp
        String preStep = null;
        String summary = null;
        List <TestMetrcisEntry> entryList = new ArrayList <> ();
        while ((line = br.readLine ()) != null) {
            TestMetrcisEntry entry = new TestMetrcisEntry ();
            // get package name filed
            if (line.contains ("package")) {
                packageName = line.replace ("package", "");
            }
            entry.setPackageName (packageName);
            // get pre-step
            if (line.contains ("@BeforeMethod")) {
                // enter locate test case..
                // load the test case to a list string
                int counter = 0;
                List <String> testCase = new ArrayList <> ();
                while ((line = br.readLine ()) != null) {
                    testCase.add (line);
                    if (line.contains ("{")) {
                        counter++;
                    } else if (line.contains ("}")) {
                        counter--;
                    }
                    if (counter == 0) {
                        break;
                    }
                }
                // operate on the test case list
                StringBuilder sb = new StringBuilder ();
                counter = 1;
                for (String tcLine: testCase) {
                    if (tcLine.contains (preConditionKeyWord))
                        sb.append (counter).append (".").append (tcLine.replace (preConditionKeyWord, "").replace ("        ", "").replace ("//", "")).append ("\n");
                }
                preStep = sb.toString ();
            }
            entry.setPreSteps (preStep);
            // get test summary
            if (line.contains ("TC#")) {
                summary = line.substring (line.indexOf ("TC#") + 4);                
                count++;
                entry.setSummary (summary);
                entry.setTestNumber (count);
                                                               
                int counter = 2;
                List <String> testCase = new ArrayList <> ();
                while ((line = br.readLine ()) != null) {
                    testCase.add (line);
                    if (line.contains ("{")&&line.contains ("test")) {
                        counter--;
                    } else if (line.contains ("}")) {
                        counter--;
                    }
                    if (counter == 0) {
                        break;
                    }
                }
                
                StringBuilder sb = new StringBuilder ();
                StringBuilder sb2 = new StringBuilder ();
                counter=1;
                int counter2=1;
                for(int i=0;i<testCase.size ();i++){
                    // set priority..
                    if(testCase.get (i).contains ("@Test")){
                        String pri=testCase.get (i);
                        entry.setPriority (pri.substring (pri.indexOf ("{") + 1, pri.indexOf ("}")));
                        //set known issue
                        String testName = testCase.get (i+1);
                        if (testName.contains ("RPW")) {
                            entry.setKnownBugs (testName.substring (testName.indexOf ("RPW"), testName.indexOf (")") - 1));
                        }
                        // get automation test case name
                        entry.setAutomationCaseName (testName.substring (testName.indexOf ("void") + 5, testName.indexOf ("(")));
                    }
             
                    //set test step
                    if(testCase.get (i).contains (stepKeyWord)){
                        sb.append (counter).append (".").append (testCase.get (i).replace (stepKeyWord, "").replace (":", "").replace ("*", "")).append ("\n");
                        counter++;
                        
                    }
                    if (testCase.get (i).contains (verifyKeyWord) ) {
                        sb2.append (counter2).append (".").append (testCase.get (i).replace (verifyKeyWord, "").replace (":", "").replace ("*", "")).append ("\n");
                        counter2++;
                    }
                    
                }
                entry.setTestSteps (sb.toString ());
                entry.setExpectResults (sb2.toString ());                
                entryList.add (entry);
                summary = null;
                System.out.println (entry.toString ());
            }     
        }
        // Done with the file
        br.close ();
        return entryList;
    }

    /**
     * @param entryList
     */
    private static void writeToExel (List <TestMetrcisEntry> entryList, String fileName) {
        // below is to write to xls
        HSSFWorkbook workbook = new HSSFWorkbook ();
        HSSFSheet sheet = workbook.createSheet ("Sample sheet");
        Map <Integer, Object[]> data = new HashMap <Integer, Object[]> ();
        data.put (1, new Object[] { "Test Number", "Summary", "Pre Steps", "Test Steps", "Verification Steps", "Priority", "Known Issue", "Package", "Automation", "Note" });
        int rowNum = 1;
        for (TestMetrcisEntry entry: entryList) {
            rowNum++;
            data.put (rowNum,
                    new Object[] { entry.getTestNumber (), entry.getSummary (), entry.getPreSteps (), entry.getTestSteps (), entry.getExpectResults (), entry.getPriority (), entry.getKnownBugs (),
                            entry.getPackageName (), entry.getAutomationCaseName (), entry.getNote () });
        }
        configExcel (sheet, data);
        writeToFile (workbook, fileName);
    }

    /**
     * @param workbook
     */
    private static void writeToFile (HSSFWorkbook workbook, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream (new File (fileName));
            workbook.write (out);
            out.close ();
            System.out.println ("Excel written successfully..");
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    /**
     * @param sheet
     * @param data
     */
    private static void configExcel (HSSFSheet sheet, Map <Integer, Object[]> data) {
        Set <Integer> keyset = data.keySet ();
        int rownum = 0;
        for (int key: keyset) {
            Row row = sheet.createRow (rownum++);
            Object[] objArr = data.get (key);
            int cellnum = 0;
            for (Object obj: objArr) {
                Cell cell = row.createCell (cellnum++);
                if (obj instanceof Date)
                    cell.setCellValue ((Date) obj);
                else if (obj instanceof Boolean)
                    cell.setCellValue ((Boolean) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue ((Integer) obj);
                else if (obj instanceof String)
                    cell.setCellValue ((String) obj);
                else if (obj instanceof Double)
                    cell.setCellValue ((Double) obj);
            }
        }
    }
}
