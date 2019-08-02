<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
    .bar {
    height: 18px;
    background: green;
    }
    </style>
</head>
<body>

<%--<div id="p" class="easyui-panel" style="width:500px;height:200px;padding:10px;"
     title="My Panel" iconCls="icon-save" collapsible="true">
    The panel content
</div>--%>
<form action="#" method="post" >
    <input type="button" value="查询" onclick="doQueryBmxx()"/><br>
    <s:iterator value="#request.list" var="b">
        <s:property value="#b.bmmc"/>
    </s:iterator>
</form>

<script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>

<script type="text/javascript">
    function doQueryBmxx() {
        $.ajax({
            url : 'doQueryBmxx.action',
            data: null,
            success: function (j) {

                //alert(j.success);
            }
        });
    }

</script>

</body>