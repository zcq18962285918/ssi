package com.yg.zero.bmgl.pojo;

public enum ExcelTypeEnum {

    XLS(".xls"), XLSX(".xlsx");

    private String value;

    ExcelTypeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
