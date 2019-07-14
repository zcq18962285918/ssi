<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ja"/>
    <meta http-equiv="Content-Script-Type" content="text/javascript"/>
    <meta http-equiv="Content-Style-Type" content="text/css"/>

    <script type="text/javascript" src="../../jquery-3.4.1.min.js"></script>
    <title>upload</title>

</head>
<body>

<div class="jquery-fileupload">
    <div class="">
        <div>
            <img id="image" src="">
        </div>
        <div id="progress">
            <div class="bar" style="width: 0%;"></div>
        </div>
        <input id="download" type="file" name="download" style="display: none"/>
        <button id="chooseFile">+选择文件</button>
        <button id="uploadFile">~开始下载</button>
    </div>
</div>

</body>
</html>