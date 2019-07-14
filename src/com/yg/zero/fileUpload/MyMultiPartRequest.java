package com.yg.zero.fileUpload;

import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * struts配置
 */
public class MyMultiPartRequest extends JakartaMultiPartRequest {
    @Override
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        String url = request.getRequestURI().toString();//取得请求的URL
        if (url.indexOf("upload.action") > 0 || url.indexOf("uploadChunk.action") > 0) {//调用的是upload.action方法
            //不需要struts2的处理
        } else {
            //需要struts2的处理,调用回父类的方法
            super.parse(request, saveDir);
        }
    }
}
