package com.yg.zero.excel;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.*;

import java.io.IOException;
import java.io.OutputStream;

public class JxlExcel {

    //Excel导出
    public OutputStream exportexcel(HtVo vo, OutputStream outputStream) throws IOException, WriteException {
        //workbook
        WritableWorkbook writableWorkbook = Workbook.createWorkbook(outputStream);
        //sheet
        WritableSheet writableSheet = writableWorkbook.createSheet(vo.getName(),0);
        //标题行合并
        writableSheet.mergeCells(0,0,5,1);
        writableSheet.addCell(new Label(0,0,"张家港孚宝仓储有限公司商务合同表"));

        WritableFont wf_title = new WritableFont(WritableFont.createFont("仿宋_GB2312"), 10,
                WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
                jxl.format.Colour.BLACK);
        WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // 单元格定义
        wcf_title.setBackground(jxl.format.Colour.WHITE); // 背景颜色
        wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // 水平对齐方式
        wcf_title.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); // 垂直对齐
        wcf_title.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK);

        int i = 2;
        for (; i < 6; i++){
            writableSheet.mergeCells(0,i,2,i);
            writableSheet.mergeCells(3,i,5,i);
        }

        writableSheet.addCell(new Label(0,2,"RCode"));
        writableSheet.addCell(new Label(0,3,"CName",wcf_title));
        writableSheet.addCell(new Label(0,4,"SName"));
        writableSheet.addCell(new Label(0,5,"已批准"));

        writableSheet.addCell(new Label(3,2,vo.getRNumber(),wcf_title));
        writableSheet.addCell(new Label(3,3,vo.getCName()));
        writableSheet.addCell(new Label(3,4,vo.getsName()));
        writableSheet.addCell(new Label(3,5,vo.getAb()));

        //写
        writableWorkbook.write();
        writableWorkbook.close();

        return outputStream;
    }
}
