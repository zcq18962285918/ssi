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


</script>

</body>
</html>
