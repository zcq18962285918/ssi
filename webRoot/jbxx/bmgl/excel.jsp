<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../easyui/themes/icon.css">
    <script type="text/javascript" src="../../easyui/jquery.min.js">
    </script>
    <script type="text/javascript" src="../../easyui/jquery.easyui.min.js">
    </script>
</head>
<body>

<a href="javascript:void(0);" onclick="exportExcel();">导出excel</a><br>

<form action="excelUpload.action" method="post" enctype="multipart/form-data">
    文件:<input type="file" name="file"/>
    <input type="submit" value="upload"/>
</form>

<script type="text/javascript">

    function exportExcel(options) {
        var defaults = {
            url: "exportBmxxExcel.action",
        };
        this.downloadFileByForm($.extend(defaults, options));
    }

    function downloadFileByForm(options) {
        $.messager.confirm("确认框", "导出EXCEL？", function(b) {
            if (!b) {
                return false;
            }
            var defaults = {
                url: undefined,
                params: undefined
            };
            defaults = $.extend(defaults, options);
            var form = $("<form></form>")
                .attr("action", defaults.url)
                .attr("method", "post");
            var ps = defaults.params;
            for(var key in ps) {
                form.append($("<input></input>")
                    .attr("type", "hidden")
                    .attr("name", key)
                    .attr("value", ps[key]));
            }
            form.appendTo('body').submit().remove();
        });
    }

    /*function uploadFile() {
        $("#uploadfile").fileinput({
            language: 'zh', //设置语言
            uploadUrl: $("body").attr("data-url")+"/permission/roleUpload!upload.action", //上传的地址
            allowedFileExtensions: ['xls', 'xlsx'],//接收的文件后缀
            //uploadExtraData:{"id": 1, "fileName":'123.mp3'},
            uploadAsync: true, //默认异步上传
            showUpload: true, //是否显示上传按钮
            showRemove : true, //显示移除按钮
            showPreview : true, //是否显示预览
            showCaption: false,//是否显示标题
            browseClass: "btn btn-primary", //按钮样式
            dropZoneEnabled: false,//是否显示拖拽区域
            //minImageWidth: 50, //图片的最小宽度
            //minImageHeight: 50,//图片的最小高度
            //maxImageWidth: 1000,//图片的最大宽度
            //maxImageHeight: 1000,//图片的最大高度
            //maxFileSize: 0,//单位为kb，如果为0表示不限制文件大小
            //minFileCount: 0,
            maxFileCount: 10, //表示允许同时上传的最大文件个数
            enctype: 'multipart/form-data',
            validateInitialCount:true,
            previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
            msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
        });
    }*/

</script>

</body>
</html>
