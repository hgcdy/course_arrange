require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        if (classId == "null") {
            classId = null;
        }
        if (studentId == "null") {
            studentId = null;
        }
        if (teacherId == "null") {
            teacherId = null;
        }


        $.ajax({
            url: "/nin-arrange/getInfo",
            dataType: "json",
            type: "post",
            data: {
                weekly: null,
                classId: classId,
                studentId: studentId,
                teacherId: teacherId
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


        //选课
        $("#details").click(function () {
            window.location.href = "http://localhost:8080/nin-" + type +"-course?" + type + "Id=" + teacherId;
        })
        $("#formButton").click(function (){
            window.location.href = window.location.href;
        })
        //返回
        $("#back").click(function (){
            window.location.href = "http://localhost:8080/nin-" + type;
        })


    })
})