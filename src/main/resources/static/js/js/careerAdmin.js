require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        var img1 = "增加图表";
        var img2 = "删除图标";
        var img3 = "修改图标";


        function record() {

        }


        //获取专业列表
        $.ajax({
            url: "nin-career/getCareerClassList",
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {

                    //todo
                    // //生成专业管理列表
                    // for (let key in data.data) {
                    //     var value = data.data[key];
                    //     $("<span></span>")
                    // }

                    //生成专业复选框
                    for (let key1 in data.data) {
                        var value1 = data.data[key1];
                        var input1 = $("<input type='checkbox' class='career'>").attr("id", key1);
                        var label1 = $("<label for=" + key1 + "><h5>" + key1 + "</h5></label><br>");
                        $("#careerCheckbox").append(input1);
                        $("#careerCheckbox").append(label1);
                        for (let i = 0; i < value1.length; i++) {
                            var input2 = $("<input type='checkbox' class='career'>").attr("name", key1).attr("id", value1[i].id);
                            var label2 = $("<label for=" + value1[i].id + ">" + value1[i].careerName + "</label><br>");
                            $("#careerCheckbox").append(input2);
                            $("#careerCheckbox").append(label2);
                        }
                        $("#" + key1).click(function () {
                            if ($("#" + key1).is(':checked')) {
                                $("input[name=" + key1 + "]").prop('checked', true);
                            } else {
                                $("input[name=" + key1 + "]").prop('checked', false);
                            }
                        })
                    }

                }
            }
        })

        $("#career").click(function () {
            if ($("#career").is(':checked')) {
                $("input[class='career']").prop('checked', true);
            } else {
                $("input[class='career']").prop('checked', false);
            }
        })

        //获取课程列表
        $.ajax({
            url: "nin-course/getSelectCourseList",
            type: "post",
            dataType: "json",
            data: {
                sign: 1
            },
            success: function (data) {
                if (data.code == 200) {
                    var list = data.data;
                    for (let i = 0; i < list.length; i++) {
                        var input = $("<input type='checkbox' class='course'>").attr("id", list[i].id);
                        var label = $("<label for=" + list[i].id + ">" + list[i].courseName + "</label><br>");
                        $("#courseCheckbox").append(input);
                        $("#courseCheckbox").append(label);
                    }
                }
            }
        })

        $("#course").click(function () {
            if ($("#course").is(':checked')) {
                $("input[class='course']").prop('checked', true);
            } else {
                $("input[class='course']").prop('checked', false);
            }
        })

        //确定添加
        $("#confirm").click(function () {
            var $career = $(".career");
            var career = "[";
            for (let i = 0; i < $career.length; i++) {
                if ($($career[i]).is(':checked')) {
                    if (!isNaN($($career[i]).attr("id"))) {
                        career = career + "," + $($career[i]).attr("id");
                    }
                }
            }
            career += "]";
            var $course = $(".course");
            var course = "[";
            for (let i = 0; i < $course.length; i++) {
                if ($($course[i]).is(':checked')) {
                    if (!isNaN($($course[i]).attr("id"))) {
                        course = course + "," + $($course[i]).attr("id");
                    }
                }
            }
            course += "]";
            $.ajax({
                url: "/nin-career-course/addBatchCourse",
                type: "post",
                dataType: "json",
                data: {
                    careerIds: career,
                    courseIds: course
                },
                success: function (data) {
                    util.hint(data.msg);

                }
            })
        })


    })
})