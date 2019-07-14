<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="java"/>
    <meta http-equiv="Content-Script-Type" content="text/javascript"/>
    <meta http-equiv="Content-Style-Type" content="text/css"/>

    <link rel="stylesheet" href="../../jQuery-File-Upload-master/commonCssJs/bootstrap.min.css">
    <script type="text/javascript" src="../../jquery-3.4.1.min.js"></script>
    <title>upload</title>

</head>
<body>
<form action="#" method="post">
    <div>
        <span class="red">*</span>上传文件:
        <div>
            <input id="fileUpload" type="file" name="file" enctype="multipart/form-data">
            <span style="line-height:30px;">文件在1个G以内</span>
            <div class="showImg">
                　　<img src="" alt=""/>
            </div>
        </div>
        <br/>
        上传进度：<br/>
        <progress id="prosbar" max="99">
        </progress>
        <br/>
        <p id="progress">0 bytes</p>
        <p id="info"></p>
    </div>
    <button type="button" onclick="upload()">上传</button>
    <input type="reset" value="重 置">
</form>

<script type="text/javascript">

    //绑定所有type=file的元素的onchange事件的处理函数
    $(':file').change(function () {
        var file = this.files[0]; //假设file标签没打开multiple属性，那么只取第一个文件就行了
        var filename = file.name;
        var size = file.size;
        var type = file.type;
        var url = window.URL.createObjectURL(file); //获取本地文件的url，如果是图片文件，可用于预览图片
        var pvImg = new Image();
        pvImg.src = url;
        pvImg.setAttribute('id', 'previewImg');
        $('.showImg').html('').append(pvImg);
        $("#info").html("文件大小: " + size + "bytes");
        $(this).next().html("文件名：" + filename + " 文件类型：" + type + " 文件大小：" + size + " url: " + url);
    });

    function upload() {

        everylisten();

        //创建FormData对象，初始化为form表单中的数据。需要添加其他数据可使用formData.append("property", "value");
        var formData = new FormData($('form')[0]);

        //ajax异步上传
        $.ajax({
            url: "upload.action",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function (result) {
                alert(result.info);
            },
            contentType: false, //必须false才会自动加上正确的Content-Type
            processData: false  //必须false才会避开jQuery对 formdata 的默认处理
        });

        return false;
    }

    //进度条显示
    var everylisten = function () {

        $.ajax({
            url: 'progress.action',
            method: 'GET',
            timeout: 120000,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (result) {
                var pro = result.progressLister;
                if (pro != null) {
                    if (pro.complete) {
                        //将进度条长度设为100
                        $("#prosbar").attr({"value": 100});
                        clearTimeout(everylisten);
                        clearProgressSession();
                    } else {
                        console.log(pro.percent+"%");
                        $("#prosbar").attr({"value": pro.percent});
                        $("#progress").attr(pro.currentLength + "bytes");
                        setTimeout(everylisten, 500);
                    }
                }
            }
        });
    };

    //清除session
    function clearProgressSession(){
        $.ajax({
            url : 'clearProgressSession.action',
            method : 'GET',
            timeout : 120000,
            contentType : "application/json; charset=utf-8",
            dataType : "json",
            success : function(result) {
            }
        });
    }
</script>
</body>
</html>