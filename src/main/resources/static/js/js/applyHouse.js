require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        var houseType = null;
        var seatMin = null;
        var seatMax = null;
        var teacherId = null;
        var classIdList = null;
        var weekly = 1;
        //补课时间教室列表
        var map = null;


        //下拉框
        $("#dropupHouseTypeButton").next("ul").find("a").click(function () {
            $("#dropupHouseTypeButton").text($(this).text()).attr("data-code", $(this).attr("data-code"));
        })
        //教师选择下拉框
        $("#dropupTeacherButton").click(function () {
            $.ajax({
                url: "nin-teacher/getTeaAll",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupTeacherButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i].teacherName).attr("teacher-id", list[i].id).click(function () {
                                $("#dropupTeacherButton").text($(this).text()).attr("teacher-id", $(this).attr("teacher-id"));
                                $("#apply td:eq(5)").attr("id", $("#dropupTeacherButton").attr("teacher-id")).text($("#dropupTeacherButton").text());
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupTeacherButton").next("ul").append($li);
                        }
                    }
                }
            })

            $("#apply td:eq(9)").attr("id", null).empty().append("<input type='button' value='选择课程' id='input1'>");
            $("#apply td:eq(1)").attr("id", null).text("");
            $("#apply td:eq(3)").attr("id", null).text("");
            $("#time-form td").removeClass("bg-success").attr("id", null);
            $("#house-select tbody").empty();
            map = null;
            $("#input1").click(function () {
                input1();
            })

        })
        //班级弹窗
        $("#dropupClassButton").click(function () {
            $("#classWindow").css("display", "block");
            $.ajax({
                url: "nin-class/collegeCareerClassList",
                type: "get",
                dataType: "json",
                success: function (data) {
                    if (data.code == 200) {
                        $("#classWindow tbody").empty();
                        for (let key in data.data) {
                            var value = data.data[key];
                            $("#classWindow tbody").append("<tr><td colspan='6'><h4>" + key + "</h4></td></tr>");
                            for (let key1 in value) {
                                var value1 = value[key1];
                                $("#classWindow tbody").append("<tr><td colspan='6'><h5>" + key1 + "</h5></td></tr>");
                                var tr = $("<tr></tr>");
                                for (let i = 0, j = 0; i < value1.length; i++, j++) {
                                    var input = $("<input type='checkbox'>").attr("id", value1[i].classId).attr("name", value1[i].className);
                                    var label = $("<label for=" + value1[i].classId + ">" + value1[i].className + "</label>");
                                    var td = $("<td></td>").append(input, label);
                                    tr.append(td);
                                    if (j == 5) {
                                        $("#classWindow tbody").append(tr);
                                        j = 0;
                                        tr = $("<tr></tr>")
                                    }
                                }
                                $("#classWindow tbody").append(tr);
                            }
                        }
                    }
                }
            })

            $("#apply td:eq(9)").attr("id", null).empty().append("<input type='button' value='选择课程' id='input1'>");
            $("#apply td:eq(1)").attr("id", null).text("");
            $("#apply td:eq(3)").attr("id", null).text("");
            $("#time-form td").removeClass("bg-success").attr("id", null);
            $("#house-select tbody").empty();
            map = null;
            $("#input1").click(function () {
                input1();
            })


        })

        $("#confirm").click(function () {
            var $input = $("input[type='checkbox']");
            var classIdList = "[";
            var classNameList = ""
            for (let i = 0; i < $input.length; i++) {
                if ($($input[i]).is(':checked')) {
                    classIdList += "," + $($input[i]).attr("id");
                    classNameList += $($input[i]).attr("name") + "/";
                }
            }
            classIdList += "]";
            if (classNameList.length > 0) {
                classNameList = classNameList.slice(0, classNameList.length - 1);
            }
            $("#apply td:eq(7)").attr("id", classIdList).text(classNameList);
            $("#classWindow").css("display", "none");
        })

        //查询按钮
        $("#query").click(function () {
            houseType = $("#dropupHouseTypeButton").attr("data-code");
            if ($.trim(houseType) == "教室类型") {
                houseType = null;
            }

            $("#apply td:eq(1)").attr("id", null).text("");

            $("#apply td:eq(3)").attr("id", null).text("");

            $("#house-select tbody").empty();

            teacherId = $("#dropupTeacherButton").attr("teacher-id");
            // $("#apply td:eq(5)").attr("id", teacherId).text($("#dropupTeacherButton").text());

            classIdList = $("#apply td:eq(7)").attr("id");

            seatMin = $("#seat1").val();

            seatMax = $("#seat2").val();

            weekly = $("#weekly").val();

            if (isNaN(seatMin) || isNaN(seatMax)) {
                util.hint("请输入正确的座位数！");
                return;
            }

            if (teacherId == null) {
                console.log("教师空");
                util.hint("请选择教师！");
                return;
            }

            if ($("#apply td:eq(7)").attr("id") == null) {
                console.log("班级空");
                util.hint("请选择班级！");
                return;
            }

            if ($("#weekly").val() == null) {
                util.hint("请选择周次！");
                return;
            }

            $("#time-form td").removeClass("bg-success").attr("id", null);
            map = null;
            $.ajax({
                url: "/nin-arrange/getLeisure",
                type: "post",
                dataType: "json",
                data: {
                    houseType: houseType,
                    classIdList: classIdList,
                    teacherId: teacherId,
                    seatMax: seatMax,
                    seatMin: seatMin,
                    weekly: weekly
                },
                success: function (data) {
                    if (data.code == 200) {
                        map = data.data;
                        for (let key in data.data) {
                            var strings = key.split("");
                            var x = strings[0];
                            var y = strings[1];
                            var $tr = $("#time-form tr");
                            var $td = $($tr[y]).find("td");
                            $($td[x - 1]).attr("id", key).attr("class", "bg-success");
                        }
                    }

                }
            })

            $("#time-form td").click(function () {
                $("#house-select tbody").empty()
                var id = $(this).attr("id");
                $("#apply td:eq(3)").attr("id", null).text("");
                $("#apply td:eq(9)").attr("id", null).empty().append("<input type='button' value='选择课程' id='input1'>");
                $("#input1").click(function () {
                    input1();
                })
                if (id != null && id != "") {

                    var split = id.split("");
                    var s = util.timeString(weekly, split[0], split[1]);

                    $("#apply td:eq(1)").attr("id", id).text(s);
                    var list = map[id];
                    for (let i = 0; i < list.length; i++) {
                        $("#house-select tbody").append("<tr><td id= " + list[i].id + " > " + list[i].houseName + " </td></tr>")
                    }

                    $("#house-select td").click(function () {
                        $("#apply td:eq(9)").attr("id", null).empty().append("<input type='button' value='选择课程' id='input1'>");
                        $("#apply td:eq(3)").attr("id", $(this).attr("id")).text($(this).text());
                        $("#input1").click(function () {
                            input1();
                        })
                    })
                } else {
                    $("#house-select tbody").append("<tr><td>无可选教室</td></tr>")
                }

            })
        })


        function input1() {
            // 参数：教师id,教室id,班级列表
            // 根据教师-课程表获取课程，去掉不符合的教室类型，且班级列表中必须有选该课程
            // 返回课表列表，如果无则提示无可选班级
            if ($("#apply td:eq(3)").attr("id") == null) {
                util.hint("请选择教室");
                return;
            }
            $.ajax({
                url: "/nin-course/getSelectApplyList",
                type: "post",
                dataType: "json",
                data: {
                    teacherId: $("#apply td:eq(5)").attr("id"),
                    houseId: $("#apply td:eq(3)").attr("id"),
                    classIdList: $("#apply td:eq(7)").attr("id")
                },
                success: function (data) {
                    if (data.code == 200) {
                        //.....
                        var courseList = data.data;

                        var $select = $("<select style='height: 100%;width: 30%;'></select>");
                        var $op = $("<option disabled='disabled' selected='selected'></option>");
                        $($select).append($op);
                        for (let i = 0; i < courseList.length; i++) {
                            var $option = $("<option></option>").text(courseList[i].courseName).val(courseList[i].id);
                            $select.append($option);
                        }
                        $("#apply td:eq(9)").append($select);
                        $("#input1").remove();
                    } else {
                        util.hint(data.msg);
                    }

                }
            })

        }
        $("#input1").click(function () {
            input1();
        })


        //确认申请按钮
        $("#affirm-apply").click(function () {
            // 获取申请表格里的信息
            // 生成一条排课信息
            var time = $("#apply td:eq(1)").attr("id");
            var houseId = $("#apply td:eq(3)").attr("id");
            var teacherId = $("#apply td:eq(5)").attr("id");
            var classIdList = $("#apply td:eq(7)").attr("id");
            var courseId = $("select").val();
            if (teacherId == null) {
                util.hint("请选择教师");
                return;
            }
            if (classIdList == null) {
                util.hint("请选择班级");
                return;
            }
            if (time == null) {
                util.hint("请点击查询后选择时间");
                return;
            }
            if (houseId == null) {
                util.hint("请选择教室");
                return;
            }
            if (courseId == null) {
                util.hint("请选择课程");
                return;
            }

            $.ajax({
                url: "/nin-arrange/addArrange",
                type: "post",
                dataType: "json",
                data: {
                    weekly: weekly,
                    week: time.split("")[0],
                    pitchNum: time.split("")[1],
                    houseId: houseId,
                    classIdList: classIdList,
                    teacherId: teacherId,
                    courseId: courseId
                },
                success: function (data) {
                    util.hint(data.msg);
                }
            })
        })



        //重置按钮
        $("#reset").click(function () {
            $("#time-form td").removeClass("bg-success").attr("id", null);
            $("#house-select tbody").empty();
            map = null;

            houseType = null;
            $("#dropupHouseTypeButton").text("教室类型").attr("data-code", "");

            teacherId = null;
            $("#dropupTeacherButton").text("* 教师").removeAttr("teacher-id");
            $("#apply td:eq(5)").attr("id", null).text("");

            $("#apply td:eq(1)").attr("id", null).text("");

            $("#apply td:eq(3)").attr("id", null).text("");

            $("#apply td:eq(7)").attr("id", null).text("");

            $("#span1").text("");
            $("#apply td:eq(9)").attr("id", null).empty().append("<input type='button' value='选择课程' id='input1'>");


            seatMin = null;
            $("#seat1").val(null);

            seatMax = null;
            $("#seat2").val(null);
        })

    })
})