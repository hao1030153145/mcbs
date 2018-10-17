package com.transing.dpmbs.util;

import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.web.exception.MySystemCode;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static List<String> readExcelFirstRow(String path) throws IOException{

        File file = new File(path);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String> list = new ArrayList<String>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //循环除了第一行的所有行
                Row row = sheet.getRow(0);
                if(row == null){
                    continue;
                }
                //获得当前行的开始列
                int firstCellNum = row.getFirstCellNum();
                //获得当前行的列数
                int lastCellNum = row.getPhysicalNumberOfCells();
                //循环当前行
                for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                    Cell cell = row.getCell(cellNum);
                    if(null == cell)
                        continue;
                    String cellValue = getCellValue(cell);

                    list.add(cellValue);

                }

            }
        }
        return list;

    }

    public static List<String[]> readExcel(String path) {

        File file = new File(path);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;

    }

    public static int readExcelRowNum(String path) throws Exception {

        try {
            File file = new File(path);
            //获得Workbook工作薄对象
            Workbook workbook = getWorkBook(file);
            //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
            int excelLenght = 0;
            if(workbook != null){
                for(int sheetNum = 0;sheetNum < 1;sheetNum++){
                    //获得当前sheet工作表
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    if(sheet == null){
                        continue;
                    }
                    //获得当前sheet的结束行
                    excelLenght = sheet.getLastRowNum();
                }
            }
            return excelLenght;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }



    public static Workbook getWorkBook(File file) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook wb = null;
        try {
            //获取excel文件的io流
            InputStream in = new FileInputStream(file);
            wb = WorkbookFactory.create(in);

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }



    /**
     * Get first row list data from Excel 2010
     *
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public static List<String> readXlsx(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            List<String> list = new ArrayList<>();
            // Read the Sheet
            for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                if (xssfSheet == null) {
                    continue;
                }
                // Read first Row
                if (xssfSheet.getLastRowNum() > 0) {
                    XSSFRow xssfRow = xssfSheet.getRow(0);
                    for (int i = 0; i < xssfRow.getLastCellNum(); i++) {
                        XSSFCell name = xssfRow.getCell(i);
                        list.add(getValue(name));
                    }
                }
            }
            return list;
        } catch (Exception e) {
            throw new WebException(MySystemCode.BIZ_EXCEL_SIZE);
        }
    }

    /**
     * Get first row list data from Excel 2003-2007
     *
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public static List<String> readXls(String path) throws IOException {
        InputStream is = new FileInputStream(path);
//        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            List<String> list = new ArrayList<>();
            // Read the Sheet
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                // Read the Row
                if (hssfSheet.getLastRowNum() > 0) {
                    HSSFRow hssfRow = hssfSheet.getRow(0);
                    for (int i = 0; i < hssfRow.getRowNum(); i++) {
                        HSSFCell name = hssfRow.getCell(i);
                        list.add(getValue(name));
                    }
                }
            }
            return list;
//        } catch (Exception e) {
//            throw new WebException(MySystemCode.BIZ_EXCEL_SIZE);
//        }
    }

    public static long readXlsxLength(String path) throws IOException {
        long length = 0;
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<String> list = new ArrayList<>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    length++;
                }
            }
        }
        return length;
    }

    public static long readXlsLength(String path) throws IOException {
        long length = 0;
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<String> list = new ArrayList<>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    length++;
                }
            }
        }
        return length;
    }

    @SuppressWarnings("static-access")
    private static String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}
