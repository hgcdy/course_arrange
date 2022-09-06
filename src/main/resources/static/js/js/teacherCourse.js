require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        const STR = ["courseName", "houseType", "must", "num"];
        query();


        //新增
        $("#insert").click(function () {
            $.ajax({
                url: "nin-course/getSelectCourseList",
                dataType: "json",
                type: "post",
                data: {
                    classId: 0
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
                    url: "nin-teacher-course/addTeacherCourse",
                    dataType: "json",
                    type: "post",
                    data: {
                        teacherId: teacherId,
                        courseId: record.courseId
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            query();
                        } else {
                            alert(data.msg);
                        }
                    }
                })
            }
        })

        //删除
        function del(id) {
            $.ajax({
                url: "nin-teacher-course/delTeacherCourse",
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
                url: "nin-teacher-course/getSelectList",
                dataType: "json",
                type: "post",
                data: {
                    teacherId: teacherId
                },
                success: function (data) {
                    if (data.code == 200) {
                        util.createForm(1, data.data, STR, 1);
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
            window.location.href = "nin-teacher";
        })


    })
})