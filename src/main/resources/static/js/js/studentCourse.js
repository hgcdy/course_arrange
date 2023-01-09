require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        const STR = ["courseName", "houseType", "must","courseTime", "startTime", "endTime", "weekTime", "cnWeek", "cnPitchNum"];
        query();
        util.navPath("/用户管理/学生管理/详情");

        //新增
        $("#insert").click(function () {
            $.ajax({
                url: "nin-course/getSelectCourseList",
                dataType: "json",
                type: "post",
                data: {
                    sign: 0
                },
                //获取课程列表
                success: function (data) {
                    if (data.code == 200) {
                        var courseList = data.data;
                        var $courseId = $("<tr><td><label for='courseId'>课程:</label></td></tr>");
                        var $td = $("<td></td>")
                        var $select = $("<select id='courseId'></select>");
                        var $op = $("<option disabled='disabled' selected='selected'></option>");
                        $($select).append($op);
                        for (let i = 0; i < courseList.length; i++) {
                            var $option = $("<option></option>").text(courseList[i].courseName).val(courseList[i].id);
                            $select.append($option);
                        }
                        $td.append($select);
                        $courseId.append($td);
                        util.popup([$courseId], ["courseId"], $insert);
                    }
                }
            })
            function $insert(record) {
                $.ajax({
                    url: "nin-student-course/addStudentCourse",
                    dataType: "json",
                    type: "post",
                    data: {
                        studentId: studentId,
                        courseId: record.courseId
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            query();
                        } else {
                            util.hint(data.msg);
                        }
                    }
                })
            }
        })

        //删除
        function del(id) {
            $.ajax({
                url: "nin-student-course/delStudentCourse",
                dataType: "json",
                type: "post",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        query();
                    }
                }
            })
        }

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-student-course/getSelectList",
                dataType: "json",
                type: "post",
                data: {
                    studentId: studentId
                },
                success: function (data) {
                    if (data.code == 200) {

                        util.createForm(1, data.data[0], STR, 1);
                        if (data.data[0].length ==0){
                            $("tbody").prepend("<tr><td colspan=6>暂无</td></tr>")
                        }
                        $("tbody").prepend("<tr><td colspan=6><h5>选修课程</h5></td></tr>")
                        $("tbody").append("<tr><td colspan=6><h5>班级必修课程</h5></td></tr>")
                        if (data.data[1].length ==0){
                            $("tbody").append("<tr><td colspan=6>暂无</td></tr>")
                        }
                        util.createForm(data.data[0].length+1, data.data[1], STR, 0);
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
            window.location.href = "nin-student";
        })

        $("#details").click(function (){
            window.location.href = window.location.href;
        })

        //课程表
        $("#formButton").click(function (){
            var str = "nin-arrange/courseForm?studentId=" + studentId + "&type=student";
            window.location.href = str;
        })

    })
})