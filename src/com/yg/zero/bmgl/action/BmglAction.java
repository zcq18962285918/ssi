package com.yg.zero.bmgl.action;

import com.yg.zero.bmgl.pojo.Bmgl;
import com.yg.zero.bmgl.service.BmglService;
import com.yg.zero.excel.DataListExcel;
import jxl.write.WriteException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts2.ServletActionContext;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要excel的导入和导出
 */
public class BmglAction {

    protected static final String EXPORT_EXCEL = "exportExcel";//导出excel
    protected String excelName;             //excel文件名
    protected InputStream excelStream;      //excel输入流

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        try {
            this.excelName = new String(excelName.getBytes("gb2312"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.excelName = "excel导出";
        }
    }

    public InputStream getExcelStream() {
        return excelStream;
    }

    public void setExcelStream(InputStream excelStream) {
        this.excelStream = excelStream;
    }

    public void setExcelStream(ByteArrayOutputStream outputStream) {
        this.excelStream = new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BmglService bmglService;

    public BmglService getBmglService() {
        return bmglService;
    }

    public void setBmglService(BmglService bmglService) {
        this.bmglService = bmglService;
    }

    private JSONObject j = new JSONObject();

    public JSONObject getJ() {
        return j;
    }

    public void setJ(JSONObject j) {
        this.j = j;
    }

    private List<Bmgl> list = new ArrayList<>();

    public List<Bmgl> getList() {
        return list;
    }

    public void setList(List<Bmgl> list) {
        this.list = list;
    }

    public String doQueryBmxx() {
        List<Bmgl> list = null;
        try {
            list = bmglService.doQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list.size() == 0){
            return "error";
        }

//        ActionContext context = ActionContext.getContext();
//        context.put("list", list);
        return "success";
    }

    public String exportBmxxExcel() {
        List<Bmgl> bmglList = null;
        try {
            bmglList = bmglService.doQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DataListExcel dataListExcel = new DataListExcel();
        ByteArrayOutputStream outputStream = null;
        dataListExcel.setSheetName("bmxx");

        String heading = "[";
        heading += "{name:'部门编码',field:'bmbm_pk'},";
        heading += "{name:'部门名称',field:'bmmc'},";
        heading += "{name:'上级部门编码',field:'sjbmbm_pk'},";
        heading += "{name:'部门编号',field:'bmbh'},";
        heading += "{name:'部门级别',field:'bmjb'},";
        heading += "{name:'是否在用',field:'sfzy'},";
        heading += "{name:'层级编号',field:'cjbh'},";
        heading += "{name:'是否事业部',field:'sfsyb'},";
        heading += "{name:'分公司编码',field:'fgsbm_pk'},";
        heading += "{name:'部门审批级别',field:'bmspjb'},";
        heading += "{name:'是否立项部门',field:'sflxbm'},";
        heading += "{name:'是否结算单位 ',field:'sfjsdw'}";
        heading += "]";

        String title = "部门信息";
        String excelTitle = "{name: '" + title + "'}";
        setExcelName(title + ".xlsx");
        try {
            dataListExcel.setTitle(excelTitle);
            dataListExcel.setHeadings(heading);
            dataListExcel.setContent(JSONArray.fromObject(bmglList));
            outputStream = dataListExcel.generateExcel();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setExcelStream(outputStream);
        return "exportExcel";
    }

    private File file;//此处应该和文件前端name对应
    private String fileFileName;//name+FIleName
    private String fileContentType;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String excelUpload() {
        JSONObject jsonObject = new JSONObject();

        if (file.length() == 0) {
            jsonObject.put("error", "上传的文件不存在");
            return "error";
        }
        if (!fileFileName.endsWith(".xls") && !fileFileName.endsWith(".xlsx")) {//判断是否为excel类型文件
            jsonObject.put("error", "上传的文件类型不正确");
            return "error";
        }

        String path = ServletActionContext.getServletContext().getRealPath("/upload");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Workbook wookbook = null;

        File desFile = new File(path);
        if (!desFile.exists())
            desFile.mkdir();
        File dFile = new File(desFile, fileFileName);

        byte[] bytes = new byte[1024];
        int len = 0;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(dFile);
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //获取绝对地址的流
            inputStream = new FileInputStream(dFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            jsonObject.put("error", "文件读取错误");
            return "error";
        }

        try {
            //workbook的创建和后缀名无关
            wookbook = WorkbookFactory.create(inputStream);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        importExcel(wookbook);

        return "success";
    }

    public String importExcel(Workbook workbook) {

        List<Bmgl> bmglList = new ArrayList<>();
        /*PoiExcel poiExcel = new PoiExcel();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            //这里写的创建workbook方法会报错是应为传进去的是两个不同版本，没有进行判断
            //poiExcel.creatWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (int m = 2; m < sheet.getLastRowNum(); m++) {
                Row row = sheet.getRow(m);
                Bmgl bmgl = new Bmgl();
                for (int n = row.getFirstCellNum() + 1; n < row.getLastCellNum(); n++) {
                    Cell cell = row.getCell(n);
                    if (n == 1) {
                        bmgl.setBmbm_pk(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 2) {
                        bmgl.setBmmc(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 3) {
                        bmgl.setPar_id(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 4) {
                        bmgl.setBmbh(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 5) {
                        bmgl.setBmjb(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 6) {
                        bmgl.setSfzy(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 7) {
                        bmgl.setCjbh(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 8) {
                        bmgl.setSfsyb(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 9) {
                        bmgl.setFgsbm_pk(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 10) {
                        bmgl.setBmspjb(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 11) {
                        bmgl.setSflxbm(cell.getStringCellValue());
                        continue;
                    }
                    if (n == 12) {
                        bmgl.setSfjsdw(cell.getStringCellValue());
                        continue;
                    }
                }
                bmglList.add(bmgl);
            }
        }

        for (Bmgl bmgl : bmglList) {
            System.out.println(bmgl.getBmbm_pk());
        }

        return "successful";
    }

}
