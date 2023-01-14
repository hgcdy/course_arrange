require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        const STR = ["courseName", "houseType", "must", "courseTime", "startTime", "endTime", "weekTime"];
        query();
        util.navPath('/用户管理/班级管理/详情');

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-class/getCourseList",
                dataType: "json",
                type: "post",
                data: {
                    classId: util.get("detailId")
                },
                success: function (data) {
                    if (data.code == 200) {
                        util.createForm(1, data.data, STR, -1);
                        $(".delete").click(function () {
                            var id = $(this).parent().parent().children("th").attr("data-id");
                            del(id);
                        })

                    }
                }
            })
        }

        //返回
        $("#back").click(function (){
            window.location.href = "nin-class?token=" + util.getToken();
        })

        $("#details").click(function (){
            window.location.href = window.location.href;
        })

        //课程表
        $("#formButton").click(function (){
            window.location.href = "nin-arrange/courseForm?token=" + util.getToken();
        })

    })
})