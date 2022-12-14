require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        const STR = ["courseName", "houseType", "must", "courseTime", "startTime", "endTime", "weekTime"];
        query();

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-class/getCourseList",
                dataType: "json",
                type: "post",
                data: {
                    classId: classId
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
            window.location.href = "nin-class";
        })

        $("#details").click(function (){
            window.location.href = window.location.href;
        })

        //课程表
        $("#formButton").click(function (){
            var str = "nin-arrange/courseForm?classId=" + classId + "&type=class";
            window.location.href = str;
        })

    })
})