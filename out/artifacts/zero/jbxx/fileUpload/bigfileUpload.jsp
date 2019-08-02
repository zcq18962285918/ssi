<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>upload</title>

    <link rel="stylesheet" type="text/css" href="../../webuploader-0.1.5/webuploader.css">

    <script type="text/javascript" src="../../jquery-3.4.1.min.js"></script>
    <script type="text/javascript" src="../../webuploader-0.1.5/webuploader.js"></script>

</head>

<body>

<div class="container" style="margin-top: 50px">
    <div id="uploader" class="container">
        <div class="container">
            <div id="fileList" class="uploader-list"></div>
            <!--存放文件的容器-->
        </div>
        <div class="btns container">
            <div id="picker" class="webuploader-container"
                 style="float: left; margin-right: 10px">
                <div>
                    选择文件 <input type="file" name="file"
                                class="webuploader-element-invisible">
                </div>
            </div>

            <div id="UploadBtn" class="webuploader-pick"
                 style="float: left; margin-right: 10px">开始上传
            </div>
            <div id="StopBtn" class="webuploader-pick"
                 style="float: left; margin-right: 10px" status="suspend">暂停上传
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var fileMd5;  //文件唯一标识
    var uploader; //全局对象uploader

    var fileName; //文件名称
    var fileSize;
    var filesArr = []; //文件数组
    var count = 0; //当前正在上传文件在文件数组中的下标

    // HOOK（钩子） 这个必须要再uploader实例化前面
    WebUploader.Uploader.register({
        "before-send-file": "beforeSendFile", //文件上传之前执行
        "before-send": "beforeSend", //文件块上传之前执行
        "after-send-file": "afterSendFile" //上传完成之后执行
    }, {
        //时间点1：文件进行上传之前调用此函数
        beforeSendFile: function (file) {
            var owner = this.owner;
            fileName = file.name; //为自定义参数文件名赋值
            fileSize = file.size;
            // 在文件开始发送前做些异步操作
            // WebUploader会等待此异步操作完成后，开始发送文件
            var deferred = WebUploader.Deferred();
            //1、计算文件的唯一标记，用于断点续传
            (new WebUploader.Uploader()).md5File(file, 0, 1024 * 1024 * 10)
                .progress(function (percentage) {
                    $('#' + file.id).find("p.state").text("正在读取文件信息...");
                    getProgressBar(file, percentage);
                })
                .then(function (val) {
                    fileMd5 = val;
                    filesArr.push(fileMd5);
                    $('#' + file.id).find("p.state").text("正在上传...");

                    $.ajax({
                        type: 'POST',
                        url: 'checkFile.action',
                        data: {
                            fileName: fileName,
                            fileMd5: filesArr[count],
                            fileSize: fileSize
                        },
                        cache: false,
                        async: false,
                        success: function (response) {

                            if (response.isWhole){
                                $('#' + file.id).find("p.state").text("文件秒传了");
                                alert("文件已存在，无需上传！");
                                owner.skipFile(file);
                                count++;
                            }

                            deferred.resolve();
                        }
                    });

                });

            return deferred.promise();
        },

        //时间点2：如果有分块上传，每一个分块发送之前执行该操作
        beforeSend: function (block) {
            var deferred = WebUploader.Deferred();
            $.ajax({
                type: 'POST',
                url: 'checkChunk.action',
                data: {
                    fileName: fileName,
                    fileSize: fileSize,
                    //文件唯一标记
                    fileMd5: filesArr[count],
                    //当前分块下标
                    chunk: block.chunk,
                    //当前分块大小
                    chunkSize: block.end - block.start
                },
                cache: false,
                //js同步
                async: false,
                dataType: 'json',
                success: function (response) {
                    if (response.ifAll){
                        //文件存在
                        deferred.reject();
                    }
                    else if (response.ifExist) {
                        //分块存在，跳过
                        deferred.reject();
                    } else {
                        //分块不存在或不完整，重新发送该分块内容
                        deferred.resolve();
                    }
                }
            });

            this.owner.options.formData.fileMd5 = fileMd5;
            this.owner.options.formData.chunk = block.chunk;
            deferred.resolve();
            return deferred.promise();
        },

        //时间点3：所有分块上传成功后调用此函数
        afterSendFile: function () {
            //如果分块上传成功，则通知后台合并分块
            $.ajax({
                type: 'POST',
                url: 'mergeChunks.action',
                data: {
                    fileName: fileName,
                    fileMd5: filesArr[count],
                    fileSize: fileSize
                },
                cache: false,
                async: false,
                success: function (response) {
                    count++;
                    if (count <= filesArr.length - 1) {
                        uploader.upload(filesArr[count].id);
                    }
                }
            });
        }
    });

    //实例化
    uploader = WebUploader.create({
        auto: false,//自动上传
        swf: '../../webuploader-0.1.5/Uploader.swf',
        server: 'uploadChunk.action',
        disableGlobalDnd: true, //禁用页面拖拽
        pick: {
            id: '#picker', //这个id是你要点击上传文件按钮的外层div的id
            multiple: true //是否可以批量上传，true可以同时选择多个文件
        },
        prepareNextFile: true,//是否允许在文件传输时提前把下一个文件准备好
        chunked: true, //分片上传
        chunkSize: 10 * 1024 * 1024, //分片大小
        chunkRetry: 3, //重传次数
        threads: 1, //上传并发数，默认3,大文件建议1
        formData: {}, //文件上传请求的参数表，每次发送都会发送此对象中的参数
        fileNumLimit: 3, //验证文件总数量, 超出则不允许加入队列
        fileSizeLimit: 1024 * 1024 * 1024 * 15, //限制文件总大小
        fileSingleSizeLimit: 1024 * 1024 * 1024 * 5, //限制单个文件大小
        resize: false //不压缩
    });

    //当有文件加入队列
    uploader.on("fileQueued", function (file) {
        $("#fileList").append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + ' <button type="button" fileId="' + file.id + '" class="btn btn-danger btn-delete">删除文件</button></h4>' +
            '<h4 class="fileSize">' + file.size + " btyes" + '</h4>' +
            '<p class="state">等待上传...</p>' +
            '<p class="progress progress-bar">上传进度...</p>' +
            '</div>');

        //每次添加文件都给btn-delete绑定删除方法
        $(".btn-delete").click(function () {
            uploader.removeFile(uploader.getFile($(this).attr("fileId"), true));
            $(this).parent().parent().fadeOut();//视觉上消失了
            $(this).parent().parent().remove();//DOM上删除了
        });

    });

    // 文件上传过程中创建进度条实时显示
    uploader.on('uploadProgress', function (file, percentage) {
        getProgressBar(file, percentage);
    });

    //发送前填充数据
    uploader.on('uploadBeforeSend', function (block, data) {
        /*var deferred = WebUploader.Deferred();
        console.log(block); // block为分块数据
        var file = block.file; // file为分块对应的file对象
        var fileMd5 = file;

        // 修改data可以控制发送哪些携带数据
        console.info("fileName= " + file.name + " fileMd5= " + fileMd5 + " fileId= " + file.id);
        // 将存在file对象中的md5数据携带发送过去
        data.md5value = fileMd5;
        data.fileName = data.name;
        data.isChunked = block.chunks > 1;*/
    });

    uploader.on('uploadSuccess', function (file) {
        $("#" + file.id).find('p.state').text('已上传');
        $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-success");
        $('#' + file.id).find(".info").find('.btn').fadeOut('slow');//上传完后删除"删除"按钮
        $('#StopBtn').fadeOut('slow');
    });

    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
        //上传出错后进度条变红
        $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-danger");
        //添加重试按钮
        //为了防止重复添加重试按钮，做一个判断
        //var retrybutton = $('#' + file.id).find(".btn-retry");
        //$('#' + file.id)
        if ($('#' + file.id).find(".btn-retry").length < 1) {
            var btn = $('<button type="button" fileid="' + file.id + '" class="btn btn-success btn-retry"><span class="glyphicon glyphicon-refresh">重新上传</span></button>');
            $('#' + file.id).find(".info").append(btn);//.find(".btn-danger")
        }
        $(".btn-retry").click(function () {
            console.log($(this).attr("fileId"));//拿到文件id
            uploader.retry(uploader.getFile($(this).attr("fileId")));
        });
    });

    uploader.on('uploadComplete', function (file) {
        $('#' + file.id).find('.progress').fadeOut();
    });

    $("#UploadBtn").click(function () {
        uploader.upload();//上传
    });

    $("#StopBtn").click(function () {
        var status = $('#StopBtn').attr("status");
        if (status == "suspend") {
            console.log("当前按钮是暂停，即将变为继续");
            $("#StopBtn").html("继续上传");
            $("#StopBtn").attr("status", "continuous");
            console.log("当前所有文件===" + uploader.getFiles());
            console.log("=============暂停上传==============");
            uploader.stop(true);
            console.log("=============所有当前暂停的文件=============");
            console.log(uploader.getFiles("interrupt"));
        } else {
            console.log("当前按钮是继续，即将变为暂停");
            $("#StopBtn").html("暂停上传");
            $("#StopBtn").attr("status", "suspend");
            console.log("===============所有当前暂停的文件==============");
            console.log(uploader.getFiles("interrupt"));
            uploader.upload(uploader.getFiles("interrupt"));
        }
    });

    /*进度条*/
    function getProgressBar(file, percentage) {
        var $id = $("#" + file.id);
        var $percent = $id.find('.progress .progress-bar');
        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($id).find('.progress-bar');
        }
        //更新状态信息和百分比
        $id.find('p.state').text('上传中');
        $percent.css('width', percentage * 100 + '%');
    }

    /*关闭上传框窗口后恢复上传框初始状态*/
    $('#picker').on('click', function () {
        // 移除所有并将上传文件移出上传序列
        for (var i = 0; i < uploader.getFiles().length; i++) {
            // 将文件从上传序列移除
            uploader.removeFile(uploader.getFiles()[i]);
            $("#" + uploader.getFiles()[i].id).remove();
        }
        // 重置uploader
        uploader.reset();
    })
</script>

</body>
</html>