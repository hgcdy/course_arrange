require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        var count = 0;



        function getDetail() {
            var detailType = util.getDetailType();
            var detailId = util.getDetailId();
            var map = new Map;
            map.set(detailType, detailId);
            return map;
        }

        query();

        //周次下拉
        $("#count").change(function(){
            count = $(this).val();
            query();
        });

        function query() {
            $("td").text("");
            var detail = getDetail();
            $.ajax({
                url: "/nin-arrange/getInfo",
                dataType: "json",
                type: "post",
                data: {
                    count: count,
                    classId: detail.get("class"),
                    studentId: detail.get("student"),
                    teacherId: detail.get("teacher")
                },
                success: function (data) {
                    if (data.code == 200) {
                        var map = data.data;
                        if ($.isEmptyObject(map)){
                            $("td").text("暂无安排");
                        } else {
                            for (const mapKey in map) {
                                var strings = mapKey.split("");
                                var i = strings[0];
                                var j = strings[1];
                                $($($("tr")[j]).find("*")[i]).text(map[mapKey]);
                            }
                        }
                    }

                }
            })
        }


        //选课
        $("#details").click(function () {
            var type = util.getDetailType();
            window.location.href = "http://" + window.location.host + "/nin-" + type + "-course?token=" + util.getToken();
        })
        $("#formButton").click(function (){
            window.location.href = window.location.href;
        })
        //返回
        $("#back").click(function (){
            window.location.href = "http://" + window.location.host + "/nin-" + util.getDetailType() + "?token=" + util.getToken();
        })

        //课程表导出
        $("#exportForm").click(function () {
            isExport();
        })


        function isExport(){
            var subData ={};
            subData.year =new Date().getFullYear();


            var xhr = new XMLHttpRequest();
            xhr.withCredentials = true;//为请求添加Cookie
            xhr.open('GET', '/exportCourseForm?id='+ util.getDetailId() + "&type=" + util.getDetailType() + "&count=" + count + "&token=" + util.getToken(), true); // 也可以使用POST方式，根据接口
            // xhr.send("id=" + typeId + "&type=" + type );
            // xhr.setRequestHeader('content-type', 'application/json');
            xhr.setRequestHeader('content-type', 'application/octet-stream');
            xhr.responseType = 'blob'; // 返回类型blob
            // 定义请求完成的处理函数，请求前也可以增加加载框/禁用下载按钮逻辑
            xhr.onreadystatechange = function (){
                // 请求完成
                if (xhr.readyState == 4 && this.status === 200) {
                    // 返回200
                    xhr.getAllResponseHeaders(); //返回全部头信息,string
                    // console.log(xhr.getAllResponseHeaders())
                    var filename = xhr.getResponseHeader('content-disposition');
                    filename = decodeURIComponent(filename.replace('attachment; filename=', ''));
                    // console.log(filename);
                    var blob;
                    blob = this.response;
                    if ('msSaveOrOpenBlob' in navigator) {
                        window.navigator.msSaveOrOpenBlob(blob, filename);
                    } else {
                        var reader = new FileReader();
                        reader.readAsArrayBuffer(blob); // 转换为base64，可以直接放入a标签
                        reader.onload = function (e) {
                            // 转换完成，创建一个a标签用于下载
                            var a = document.createElement('a');
                            a.download = filename;
                            a.href = URL.createObjectURL(new Blob([e.target.result]));
                            a.target = 'downloadframe';
                            $('body').append(a); // 修复firefox中无法触发click
                            a.click();
                            // $("#myModalDown").modal("hide");
                            $(a).remove();
                        };
                    }
                }
            }
            var blob = new Blob([JSON.stringify(subData, null, 2)], {
                type: 'application/json'
            }); // 创建 blob 对象，同时指定类型
            xhr.send(blob);
        }

    })
})