package com.yg.zero.fileUpload.FileUploadOrDown;

import com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad.DownFileFetch;
import com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad.DownFileInfoBean;

public class Test {
    public Test() {
        try {
            DownFileInfoBean bean = new DownFileInfoBean(
                    "https://mmbiz.qpic.cn/mmbiz_jpg/lcaq0oMjdFyIOFupkNF4URY28lvnegw9oKark5icZVDjtvSrTX4H6ye9n5Oycj5a5LCkgqiciab9pGnuJ6UMF0XxQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1",
                    "E:\\temp",
                    "miao.jpg", 5, true, null);
            /*DownFileInfoBean bean = new DownFileInfoBean(
                    null,
                    "E:\\temp",
                    "01-nosql的简介_.flv", 5, false,
                    new File("C://Users//Administrator//Desktop//01-nosql的简介_.flv"));*/
            DownFileFetch fileFetch = new DownFileFetch(bean);
            fileFetch.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Test();
    }
}
