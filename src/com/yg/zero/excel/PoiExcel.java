package com.yg.zero.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PoiExcel {

    //创建excel工作簿
    public Workbook creatWorkbook(String filePath, InputStream inputStream) throws IOException {
        //这里会报异常的注意事项，无论创建的什么，流都会关闭
        if (inputStream.available() != 0) {
            if (filePath.endsWith(".xls"))
                return new HSSFWorkbook(new POIFSFileSystem(inputStream));
            if (filePath.endsWith(".xlsx"))
                return new XSSFWorkbook(inputStream);
        }
        return null;
    }

    //导入数据,数据来自excel
    public void importDataFromExcel(Workbook workbook, String FilePath){
        List<HtVo> htVoList = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (int m = 2; m < sheet.getLastRowNum(); m++){
                Row row = sheet.getRow(m);
                HtVo vo = new HtVo();
                for (int n = row.getFirstCellNum() + 1; n < row.getLastCellNum(); n++){
                    Cell cell = row.getCell(n);
                    vo.setRNumber(cell.getStringCellValue());
                    vo.setHtNuber(cell.getStringCellValue());
                    vo.setsName(cell.getStringCellValue());
                    vo.setAb(cell.getStringCellValue());
                }
                htVoList.add(vo);
            }
            for (HtVo vo : htVoList){
                System.out.println(vo.getRNumber()+"\n"+vo.getHtNuber());
            }
        }
    }

    //获取  所有内容
    public String getExcel(Workbook workbook) {
        String text = "";
        Sheet sheet = workbook.getSheetAt(0);
        for (Iterator rowIterator = sheet.iterator(); rowIterator.hasNext(); ) {
            Row row = (Row) rowIterator.next();
            for (Iterator iterator = row.cellIterator(); iterator.hasNext(); ) {
                Cell cell = (Cell) iterator.next();
                if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
                    continue;
                if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC || cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                    text += cell.getNumericCellValue();
                else text += cell.getStringCellValue();
            }
            text += "\n";
        }
        return text;
    }

    //导入excel
    public boolean importExcel(HtVo vo, Workbook workbook, String filePath) throws IOException {
        if (vo.getRNumber().isEmpty() || vo.getHtNuber().isEmpty())
            return false;
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            vo.setHtNuber(sheet.getCellComment(1, 1).toString());
            Row row = sheet.getRow(i);
            if (!vo.getRNumber().isEmpty()) {
                row.getCell(2).setCellValue(vo.getRNumber());
                continue;
            }
            if (!vo.getHtNuber().isEmpty()) {
                row.getCell(2).setCellValue(vo.getHtNuber());
                continue;
            }
            if (!vo.getAb().isEmpty()) {
                row.getCell(2).setCellValue(vo.getAb());
                continue;
            }
            if (!vo.getCName().isEmpty()) {
                row.getCell(2).setCellValue(vo.getCName());
                continue;
            }
            if (!vo.getcPEC().isEmpty()) {
                row.getCell(2).setCellValue(vo.getcPEC());
                continue;
            }
            if (!vo.getCPeriod().isEmpty()) {
                row.getCell(2).setCellValue(vo.getCPeriod());
                continue;
            }
            if (!vo.geteTC().isEmpty()) {
                row.getCell(2).setCellValue(vo.geteTC());
                continue;
            }
            if (!vo.getModeTran().isEmpty()) {
                row.getCell(2).setCellValue(vo.getModeTran());
                continue;
            }
            if (!vo.getPb().isEmpty()) {
                row.getCell(2).setCellValue(vo.getPb());
                continue;
            }
            if (!vo.getProduct().isEmpty()) {
                row.getCell(2).setCellValue(vo.getProduct());
                continue;
            }
            if (!vo.getoSC().isEmpty()) {
                row.getCell(2).setCellValue(vo.getoSC());
                continue;
            }
            if (!vo.getRemarks().isEmpty()) {
                row.getCell(2).setCellValue(vo.getRemarks());
                continue;
            }
            if (!vo.getSCharge().isEmpty()) {
                row.getCell(2).setCellValue(vo.getSCharge());
                continue;
            }
            if (!vo.getThEnt().isEmpty()) {
                row.getCell(2).setCellValue(vo.getThEnt());
                continue;
            }
            if (!vo.gettNumber().isEmpty()) {
                row.getCell(2).setCellValue(vo.gettNumber());
                continue;
            }
        }
        OutputStream outputStream = null;
        outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        outputStream.close();
        return true;
    }

    /*public static Workbook creatWorkbook(String excelfilePath, InputStream inputStream) throws IOException {
        //文档对象
        Workbook workbook = null;
        if (excelfilePath.endsWith(".xls")){
            workbook =  (inputStream == null) ? new HSSFWorkbook() : new HSSFWorkbook(new POIFSFileSystem(inputStream));
        }
        if (excelfilePath.endsWith(".xlsx")){
            workbook =  (inputStream == null) ? new SXSSFWorkbook(500) : new XSSFWorkbook(inputStream);
        }
        return  workbook;
    }*/


    /* */

    /**
     * 标题
     *//*
    public String readTitle(String excelfilePath) throws IOException {

        File file = new File(excelfilePath);
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = creatWorkbook(excelfilePath, inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int s = sheet.getFirstRowNum()+sheet.getLastRowNum();

        Row row = sheet.createRow(0);
        System.out.println(row.getFirstCellNum()+"---"+row.getLastCellNum());

        Cell cell = row.getCell(1);
        String title = cell.getStringCellValue();
        return title;
    }*/

        /*String excelFile = "d:/swht.xlsx";
        File file = new File(excelFile);
        Workbook workbook = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> m = new HashMap<>();
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            String key = row.getCell(0).getStringCellValue();
            String value = null;
            for (int j = 1; j < row.getLastCellNum(); j++){
                value = row.getCell(j).getStringCellValue();
                if (value.isEmpty())
                    continue;
            }
            m.put(key, value);
        }*/
        /*String excelFile = "d:/swht.xlsx";
        File file = new File(excelFile);
        try (InputStream inputStream = new FileInputStream(file)) {
        }
        Workbook workbook = null;
        new XSSFWorkbook(InputStream);

        workbook = new SXSSFWorkbook()



        readTitle(file);
        //readContent(file);

    }
     public static String[] readTitle(File file){
        //创建workbook

         Sheet sheet = getSheet(workbook);
         Row row = sheet.getRow(0);
         int lastCellNum = row.getLastCellNum();
         String[] title = new String[lastCellNum];
         row.getCell(0).getStringCellValue();
         return title;
     }*/

    //excel输入一个对象
    public HtVo getVo(HtVo vo, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        HtVo htVo = new HtVo();
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            htVo.setHtNuber(sheet.getCellComment(i, 3).toString());
            htVo.setRNumber(sheet.getCellComment(i, 3).toString());
        }
        return htVo;
    }











     /*public static Sheet getSheet(Workbook workbook, int sheet){
        return workbook.getSheetAt(sheet);
     }*/

}
