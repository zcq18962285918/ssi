package com.yg.zero.excel;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.*;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Boolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * excel对象和工具类
 */
public class DataListExcel {

    /**
     * 标题name： style:样式
     */
    private JSONObject title;

    /**
     * 标题行高
     */
    private int titleHeight;

    /**
     * 标题单元格样式
     */
    private WritableCellFormat titleStyle;

    /**
     * 是否显示序列号
     */
    private boolean b_showSerialNum;

    /**
     * 序号列 偏移量
     */
    private int serialNumColOffset;

    /**
     * 表头
     */
    private JSONArray headings;

    /**
     * 表头行 行高
     */
    private int headingsHeight;

    /**
     * 表头行 名称
     */
    private String[] headingNames;

    /**
     * 表头样式
     */
    private WritableCellFormat[] headingsStyle;

    /**
     * 数据内容
     */
    private JSONArray content;

    /**
     * 数据行 行高
     */
    private int contentHeight;

    /**
     * 数据行 样式
     */
    private WritableCellFormat[] contentStyle;

    /**
     * 数据行 字段名
     */
    private String[] contentNames;

    /**
     * 工作表名称
     */
    private String sheetName = "sheet";

    /**
     * 工作表下标
     */
    private int sheetIndex = 0;

    /**
     * 工作表最大数据行
     */
    private int sheetHasMaxRow = 65500;

    /**
     * 72磅=1英寸=2.54厘米=25.4毫米
     * 行高单位：磅	1毫米＝2.7682个单位；1厘米＝27.682个单位；1行高单位＝0.3612毫米。1行高单位=20jxl行高单位
     * 列宽单位：1/10英寸	1毫米＝0.4374个单位；1厘米＝4.374 个单位；1个单位＝2.2862毫米。
     * 设置10jxl列宽单位，实际9.29excel单位
     * 设置9jxl列宽单位，实际8.29excel单位
     */
    private static int psToJxlHeight = 20;
    /**
     * 磅数转 excel单位
     * 中文字体磅数 1磅=0.19单位
     * 数字 1磅=0.1202单位
     * 大写字母 1磅=0.1428单位
     */

    private static float psToWidth_ch = 0.1905F;//中文字
    private static float psToWidth_else = 0.1428F;//其他，包括英文大小写、符号、数字等

    /**
     * 列宽模式：
     * 为user，使用用户自定义列宽
     * 如果数据列中的大部分数据宽度相近或宽度较短，则设置为max，选择最宽宽度
     * 如果数据列中的数据宽度变化很大，则设置为auto，部分数据需要换行
     *
     * 默认max
     */
    private String[] colWidthMode;

    /**
     * 用户定义的列宽（单位：1/10英寸）
     */
    private int[] userColWidth;
    /**
     * 根据表头名称长度算出的列宽
     */
    private int[] headingColWidth;
    /**
     * 该列自动宽度
     */
    private int[] autoColWidth;
    /**
     * 该列最大列宽
     */
    private int[] maxColWidth;

    /**
     * 计算列宽 默认计算自动列宽
     */
    private boolean auto = true;
    private void calcColWidth(int colIndex, String content, Boolean auto){

        int size = contentStyle[colIndex].getFont().getPointSize();
        int chNum = getChineseNum(content);
        int enNum = content.length() - chNum;
        //计算列宽
        int newV = (int) (Math.ceil(enNum * size * psToWidth_else + chNum * size * psToWidth_ch) + 1);
        if (!auto.equals(true)) {
            //计算最大列宽
            int oldMV = maxColWidth[colIndex];//最大列宽
            if (newV > oldMV)
                autoColWidth[colIndex] = widthRange(newV);
        }
        int oldAV = autoColWidth[colIndex];//自动列宽
        if (newV > oldAV)
            autoColWidth[colIndex] = widthRange((int) (Math.ceil(newV + oldAV)/2));
    }

    /**
     * 限制列宽范围
     */
    private int widthRange(int w) {
        if(w < 0) {
            return 0;
        }else if(w > 255) {
            return 255;
        }
        return w;
    }

    /**
     * 统计content中是汉字的个数
     */
    public int getChineseNum(String content) {
        int lenOfChinese = 0;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");//汉字的Unicode编码范围
        Matcher m = p.matcher(content);
        while(m.find()){
            lenOfChinese++;
        }
        return lenOfChinese;
    }

    /**
     * 获取工作表号
     */
    private String getSheetNumber(){
        return (sheetIndex + 1) + "";
    }

    /**
     * 设置工作表最大数据行
     */
    public void setSheetMaxRow(int sheetMaxRow) {
        this.sheetHasMaxRow = sheetMaxRow;
    }

    /**
     * 设置工作表名称
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * 设置标题属性（可不设置标题）
     */
    public void setTitle(JSONObject title) throws WriteException {
        this.title = title;
        parseTitle();
    }

    /**
     * 设置标题属性（可不设置标题）-{name:标题名称,style:标题单元格样式,rownumbers:是否显示行号}
     */
    public void setTitle(String title) throws WriteException {
        this.title = JSONObject.fromObject(title);
        parseTitle();
    }

    /**
     * 设置表头
     */
    public void setHeadings(JSONArray headings) throws WriteException {
        this.headings = headings;
        parseHeadings();
    }
    /**
     * 设置表头-[{name:表头名称,hwcf:表头的单元格样式,wcf:数据行的单元格样式,field:字段名称,width:列宽}]  width：不填则按内容最大值
     *
     */
    public void setHeadings(String headings) throws WriteException {
        this.headings = JSONArray.fromObject(headings);
        parseHeadings();
    }

    /**
     * 设置数据内容
     */
    public void  setContent(JSONArray content) {
        this.content = content;
    }

    /**
     * 解析标题
     */
    private void parseTitle() throws WriteException {
        if(!title.containsKey("name")) {//未设置标题，则赋值
            title.put("name", "默认标题");
        }else if("Sheet".equals(sheetName)) {//有标题，且未设置工作表名称，则工作表名称默认使用标题
            sheetName = title.getString("name");
        }
        if(title.containsKey("style")) {
            titleStyle = getStyleByName(title.getString("style"));
        }else {
            titleStyle = getStyle_title();
        }
        int size = titleStyle.getFont().getPointSize();
        titleHeight = size * 3 * psToJxlHeight;//考虑到上下边距等  行高暂定3倍字体磅数
        if(title.containsKey("rownumbers")) {//默认显示序列号
            b_showSerialNum = title.getBoolean("rownumbers");
        }else {
            b_showSerialNum = true;
        }
        serialNumColOffset = b_showSerialNum ? 1 : 0;
    }

    /**
     * 根据名称获取单元格样式
     */
    private WritableCellFormat getStyleByName(String name) throws WriteException {
        if("".equals(name.trim())) {
            return getStyle_default();
        }else if(TITLE.equals(name)) {
            return getStyle_title();
        }else if(HEADING.equals(name)) {
            return getStyle_heading();
        }else if(STR.equals(name)) {
            return getStyle_str();
        }else if(STR_CENTER.equals(name)) {
            return getWcf_str_center();
        }else if(STR_RIGHT.equals(name)) {
            return getWcf_str_right();
        }else if(NUMBER.equals(name)) {
            return getWcf_number();
        }
        return getStyle_default();
    }

    public static String TITLE = "style_title";
    public static String HEADING = "style_heading";
    public static String STR = "style_str";
    public static String STR_CENTER = "wcf_str_center";
    public static String STR_RIGHT = "wcf_str_right";
    public static String NUMBER = "wcf_number";

    /**
     * 单元格样式控制对象-默认样式
     */
    private WritableCellFormat style_default;
    private WritableCellFormat getStyle_default() throws WriteException {
        if(style_default == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    9,
                    WritableFont.NO_BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            style_default = wcf;
        }
        return style_default;
    }

    /**
     * 单元格样式控制对象-标题
     */
    private WritableCellFormat style_title;
    private WritableCellFormat getStyle_title() throws WriteException {
        if(style_title == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    14,
                    WritableFont.BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            style_title = wcf;
        }
        return style_title;
    }

    /**
     * 单元格样式控制对象-表头
     */
    private WritableCellFormat style_heading;
    private WritableCellFormat getStyle_heading() throws WriteException {
        if(style_heading == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    10,
                    WritableFont.BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            style_heading = wcf;
        }
        return style_heading;
    }

    /**
     * 单元格样式控制对象-内容，默认左对齐
     */
    private WritableCellFormat style_str;
    private WritableCellFormat getStyle_str() throws WriteException {
        if(style_str == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    9,
                    WritableFont.NO_BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.LEFT); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            style_str = wcf;
        }
        return style_str;
    }

    /**
     * 单元格样式控制对象-内容，居中
     */
    private WritableCellFormat wcf_str_center;
    private WritableCellFormat getWcf_str_center() throws WriteException {
        if(wcf_str_center == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    9,
                    WritableFont.NO_BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            wcf_str_center = wcf;
        }
        return wcf_str_center;
    }

    /**
     *  单元格样式控制对象-内容，右对齐
     */
    private WritableCellFormat wcf_str_right;
    private WritableCellFormat getWcf_str_right() throws WriteException {
        if(wcf_str_right == null) {
            WritableFont wf = new WritableFont(//字体样式：字体、字号、粗体、斜体、下划线、颜色
                    WritableFont.createFont("微软雅黑"),
                    9,
                    WritableFont.NO_BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    Colour.BLACK);
            WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.RIGHT); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            wcf.setWrap(true);	//自动换行
            wcf_str_right = wcf;
        }
        return wcf_str_right;
    }

    /**
     *  单元格样式控制对象-数字，右对齐，保留2位小数
     */
    private WritableCellFormat wcf_number;
    private WritableCellFormat getWcf_number() throws WriteException {
        if(wcf_number == null) {
            NumberFormat nf = new NumberFormat("0.00##");//保留两位小数
            WritableCellFormat wcf = new WritableCellFormat(nf); // 单元格样式
            wcf.setBackground(Colour.WHITE); // 设置单元格的背景颜色
            wcf.setAlignment(Alignment.RIGHT); // 设置水平对齐方式
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置垂直对齐方式
            wcf.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); //设置边框
            //wcf.setWrap(true);	//自动换行
            wcf_number = wcf;
        }
        return wcf_number;
    }

    /**
     * 解析表头
     */
    private void parseHeadings() throws WriteException {
        int size = headings.size();
        headingNames = new String[size];
        headingsStyle = new WritableCellFormat[size];

        contentStyle = new WritableCellFormat[size];
        contentNames = new String[size];

        colWidthMode = new String[size];
        userColWidth = new int[size];
        headingColWidth = new int[size];
        autoColWidth = new int[size];
        maxColWidth = new int[size];
        for(int i = 0; i < size; i++) {
            //解析每列
            JSONObject h = headings.getJSONObject(i);
            String key = "name";//表头名称
            if(h.containsKey(key)) {
                headingNames[i] = h.getString(key);
            }else {
                headingNames[i] = "未定义";
            }
            key = "hstyle";//表头样式
            if(h.containsKey(key)) {
                headingsStyle[i] = getStyleByName(h.getString(key));
            }else {
                headingsStyle[i] = getStyle_heading();
            }
            {
                String n = headingNames[i];
                int size1 = headingsStyle[i].getFont().getPointSize();
                int chNum = getChineseNum(n);
                int enNum = n.length() - chNum;
                //根据字数和字体磅数算列宽
                headingColWidth[i] =
                        widthRange((int) Math.ceil(enNum * size1 * psToWidth_else + chNum * size1 * psToWidth_ch) + 1);
            }
            key = "style";//数据列样式
            if(h.containsKey(key)) {
                contentStyle[i] = getStyleByName(h.getString(key));
            }else {
                contentStyle[i] = getStyle_str();
            }
            key = "field";//数据列字段名称
            if(h.containsKey(key)) {
                contentNames[i] = h.getString(key);
            }else {
                contentNames[i] = "";
            }
            key = "width";//列宽度设定
            if(h.containsKey(key)) {
                String w = h.getString(key);
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(w);
                if(isNum.matches()) {
                    //如果是数字
                    colWidthMode[i] = "user";
                    userColWidth[i] = widthRange(Integer.parseInt(w));//使用自定义列宽
                }else {
                    if("auto".equals(w)) {
                        colWidthMode[i] = "auto";
                    }else if("max".equals(w)) {
                        colWidthMode[i] = "max";
                    }else {
                        colWidthMode[i] = "max";
                    }
                }
            }else {
                colWidthMode[i] = "max";
            }
        }
        int ps = headingsStyle[0].getFont().getPointSize();//暂时 默认取第一列
        headingsHeight = ps * 3 * psToJxlHeight;//考虑到上下边距等  行高暂定3倍字体磅数
        contentHeight = 9 * 3 * psToJxlHeight;
    }

    /**
     * 生成excel 此处生成的excel版本不确定，一般是03版本与生成excel文件后缀名无关
     */
    public ByteArrayOutputStream generateExcel() throws IOException, WriteException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //工作簿
        WritableWorkbook wWorkbook = Workbook.createWorkbook(stream);
        int h_size = headings.size();//表头行列数
        //没有表头行或数据行，返回空excel
        if (headings == null || h_size == 0){
            //创新的sheet
            wWorkbook.createSheet(sheetName, sheetIndex);
            wWorkbook.write();
            wWorkbook.close();
            return stream;
        }
        int c_size = content.size();//数据行大小
        WritableSheet sheet;
        while (c_size >= sheetHasMaxRow * sheetIndex){
            //拆分多sheet
            sheet = wWorkbook.createSheet(sheetName+getSheetNumber(), sheetIndex);
            int rowIndex = 0;//行号
            int serialNumCount = 1;//序号列计数
            //标题行
            if(title != null) {
                sheet.setRowView(0, titleHeight, false);//设置行高，标题行
                sheet.addCell(new Label(0, 0, title.getString("name"), titleStyle));//设置标题
                sheet.mergeCells(0, 0, h_size - 1 + serialNumColOffset, 0); //合并单元格：第1列第1行到第N列第1行
                rowIndex++;
            }
            //表头行
            sheet.setRowView(1, headingsHeight, false);//设置行高，表头行
            if(b_showSerialNum) {
                sheet.addCell(new Label(0, 1, "序号", headingsStyle[0]));
            }
            for(int i = 0; i < h_size; i++) {
                sheet.addCell(new Label(i + serialNumColOffset, 1, headingNames[i], headingsStyle[i]));
            }
            rowIndex++;
            //数据行
            int row_start = sheetHasMaxRow * sheetIndex;//每表的开始行
            int row_end = Math.min(sheetHasMaxRow * (sheetIndex + 1), c_size);//每表结束行
            for (int r = row_start; r < row_end; r++) {
                if(b_showSerialNum) {
                    sheet.addCell(new Label(0, rowIndex, "" + (serialNumCount++), getWcf_str_center()));
                }
                JSONObject row = content.getJSONObject(r);
                for(int k = 0; k < h_size; k++) {
                    String value = "";
                    String field = contentNames[k];
                    if("".equals(field.trim())) {
                        value = "未定义字段";
                    }else if(!row.containsKey(field)) {
                        value = "数据中无此字段";
                    }else {
                        value = row.getString(field);
                    }
                    WritableCellFormat wcf = contentStyle[k];
                    if(wcf == wcf_number && isDouble(value)) {//判断样式是否是数字
                        sheet.addCell(new jxl.write.Number(k + serialNumColOffset, rowIndex, Double.parseDouble(value), wcf));
                    }else {
                        sheet.addCell(new Label(k + serialNumColOffset, rowIndex, value, wcf));
                    }

                    calcColWidth(k, value, true);
                    calcColWidth(k, value, false);
                }
                sheet.setRowView(rowIndex, contentHeight, false);//设置默认行高
                rowIndex++;
            }
            //设置列宽
            for(int k = 0; k < h_size; k++) {
                if(b_showSerialNum) {
                    sheet.setColumnView(0, 6);
                }
                String mode = colWidthMode[k];
                if("user".equals(mode)) {//如果有自定义列宽，则使用自定义列宽
                    sheet.setColumnView(k + serialNumColOffset, userColWidth[k]);
                    continue;
                }else if("max".equals(mode)) {
                    int w = maxColWidth[k];
                    if(w < headingColWidth[k]) {//优先使用表头列宽
                        w = headingColWidth[k];
                    }
                    sheet.setColumnView(k + serialNumColOffset, w);
                    continue;
                }else if("auto".equals(mode)) {
                    int w = autoColWidth[k];
                    if(w < headingColWidth[k]) {//优先使用表头列宽
                        w = headingColWidth[k];
                    }
                    sheet.setColumnView(k + serialNumColOffset, w);
                    continue;
                }
            }
            sheetIndex ++;
        }
        wWorkbook.write();
        wWorkbook.close();
        return stream;
    }

    /**
     *  判断是否是double
     */
    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }catch (NumberFormatException ex){}
        return false;
    }
}