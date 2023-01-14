require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        var page = 1;
        var size = 10;
        var total = 0;
        var college = null;
        var studentName = null;
        var careerId = null;
        var classId = null;
        const STR = ["college", "careerName", "className", "studentName", "studentCode"];
        query();
        util.navPath("/用户管理/学生管理");

        //学院选择下拉框
        $("#dropupCollegeButton").click(function () {
            //重置专业选择
            careerId = null;
            $("#dropupCareerIdButton").text("专业").removeAttr("career-id");
            //重置班级选择
            classId = null;
            $("#dropupClassIdButton").text("班级").removeAttr("class-id");

            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupCollegeButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            if (list[i] == "选修") {
                                continue;
                            }
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i]).click(function () {
                                $("#dropupCollegeButton").text($(this).text());
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupCollegeButton").next("ul").append($li);
                        }
                    }
                }
            })
        })

        //专业选择下拉框
        $("#dropupCareerIdButton").click(function () {
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院") {
                college = null;
            }
            //重置班级选择
            classId = null;
            $("#dropupClassIdButton").text("班级").removeAttr("class-id");

            $.ajax({
                url: "nin-career/getCareerList",
                dataType: "json",
                type: "post",
                data: {
                    college: college
                },
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupCareerIdButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            if (list[i].id == 0 || list[i].id == -1) {
                                continue;
                            }
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i].careerName).attr("career-id", list[i].id).click(function () {
                                $("#dropupCareerIdButton").text($(this).text()).attr("career-id", $(this).attr("career-id"));
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupCareerIdButton").next("ul").append($li);
                        }
                    }
                }
            })
        })

        //班级选择下拉框
        $("#dropupClassIdButton").click(function () {

            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院") {
                college = null;
            }
            careerId = $("#dropupCareerIdButton").attr("career-id");

            $.ajax({
                url: "nin-class/getClassList",
                dataType: "json",
                type: "post",
                data: {
                    college: college,
                    careerId: careerId
                },
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupClassIdButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            if (list[i].careerId == 0) {
                                continue;//
                            }
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i].className).attr("class-id", list[i].classId).click(function () {
                                $("#dropupClassIdButton").text($(this).text()).attr("class-id", $(this).attr("class-id"));
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupClassIdButton").next("ul").append($li);
                        }
                    }
                }
            })
        })


        //查询按钮
        $("#query").click(function () {
            studentName = $("#studentName").val();
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院") {
                college = null;
            }
            careerId = $("#dropupCareerIdButton").attr("career-id");
            classId = $("#dropupClassIdButton").attr("class-id");
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            college = null;
            $("#dropupCollegeButton").text("学院");
            careerId = null;
            $("#dropupCareerIdButton").text("专业").removeAttr("career-id");
            classId = null;
            $("#dropupClassIdButton").text("班级").removeAttr("class-id");

            studentName = null;
            $("#studentName").val(null);
            query();
        })


        function query() {
            $.ajax({
                url: "nin-student/getPageSelectList",
                dataType: "json",
                type: "post",
                data: {
                    college: college,
                    studentName: studentName,
                    careerId: careerId,
                    classId: classId,
                    size: size,
                    page: page
                },
                success: function (data) {
                    if (data.code == 200) {
                        total = data.data.total;
                        if ((data.data.list).length != 0) {
                            $("#page span:eq(2) input").val(page);
                            util.createForm((page - 1) * size + 1, data.data.list, STR, 3);
                            $("#page-text").text("共" + total + "条数据, " + Math.ceil(total / size) + "页");
                            $(".delete").click(function () {
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                del(id);
                            })
                            $(".update").click(function () {
                                alter($(this).parent().parent().children("th"));
                            })
                            //详情
                            $(".details").click(function () {
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                util.setCache("detailId", id);
                                util.setCache("type", "student");
                                window.location.href = "nin-student-course?token=" + util.getToken();
                            })

                        } else {
                            if (page > 1) {
                                page = page - 1;
                                query();
                            } else {
                                $("tbody").empty();
                            }
                        }
                    }
                }
            })
        }

        //删除
        function del(id) {
            $.ajax({
                url: "nin-student/delStudent",
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

        //编辑
        function alter($id) {
            var id = $($id).attr("data-id");
            $.ajax({
                url: "nin-student/getStudentById",
                dataType: "json",
                type: "get",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var obj = data.data;
                        var $studentName = $("<tr><td><label for='studentName'>学生名称:</label></td><td><input type='text' id='studentName' value=" + obj.studentName + "></td></tr>");
                        var $studentCode = $("<tr><td><label for='studentCode'>学生账号:</label></td><td><input type='text' id='studentCode' value=" + obj.studentCode + "></td></tr>");
                        var $studentPassword = $("<tr><td><label for='studentPassword'>密码:</label></td><td><input type='text' id='studentPassword' value='******'></td></tr>");
                        var $classId = $("<tr><td><label for='classId'>班级:</label></td></tr>");
                        var $td = $("<td></td>")
                        var $select = $("<select id='classId' ></select>");
                        box($select, obj.classId);
                        $td.append($select);
                        $classId.append($td);

                        util.popup([$studentName, $studentCode, $studentPassword, $classId], ["studentName", "studentCode", "studentPassword", "classId"], $update);

                    }
                }
            })

            function $update(record) {
                if (record.studentPassword == "******") {
                    record.studentPassword = undefined;
                }
                $.ajax({
                    url: "nin-student/alterStudent",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id,
                        studentName: record.studentName,
                        studentCode: record.studentCode,
                        studentPassword: record.studentPassword,
                        classId: record.classId
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
        }

        //新增
        $("#insert").click(function () {
            var $studentName = $("<tr><td><label for='studentName'>学生名称:</label></td><td><input type='text' id='studentName'></td></tr>");
            var $studentCode = $("<tr><td><label for='studentCode'>学生账号:</label></td><td><input type='text' id='studentCode'></td></tr>");
            var $studentPassword = $("<tr><td><label for='studentPassword'>密码:</label></td><td><input type='text' id='studentPassword'></td></tr>");
            var $classId = $("<tr><td><label for='classId'>班级:</label></td></tr>");
            var $td = $("<td></td>")
            var $select = $("<select id='classId'></select>");
            var $op1 = $("<option disabled='disabled' selected='selected'></option>");
            $($select).append($op1);
            box($select);
            $td.append($select);
            $classId.append($td);

            util.popup([$studentName, $studentCode, $studentPassword, $classId], ["studentName", "studentCode", "studentPassword", "classId"], $insert);

            function $insert(record) {

                $.ajax({
                    url: "nin-student/addStudent",
                    dataType: "json",
                    type: "post",
                    data: {
                        studentName: record.studentName,
                        studentCode: record.studentCode,
                        studentPassword: record.studentPassword,
                        classId: record.classId
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

        function box($select, classId_) {
            $.ajax({
                url: "nin-class/collegeCareerClassList",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        for (let key in data.data) {
                            var value = data.data[key];
                            var $op2 = $("<option disabled='disabled'></option>").html("&nbsp;" + key);
                            $($select).append($op2);
                            for (let key1 in value) {
                                var value1 = value[key1];
                                var $op3 = $("<option disabled='disabled'></option>").html("&nbsp;&nbsp;&nbsp;" + key1);
                                $($select).append($op3);
                                for (let i = 0; i < value1.length; i++) {
                                    if (classId_ != null && classId_ == value1[i].classId) {
                                        var $op4 = $("<option selected = 'selected'></option>").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + value1[i].className).val(value1[i].classId);
                                    } else {
                                        var $op4 = $("<option></option>").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + value1[i].className).val(value1[i].classId);
                                    }
                                    $($select).append($op4);
                                }
                            }

                        }
                    }
                }
            })
        }


        //切换每页记录条数
        $("#page a:eq(0)").click(function () {
            size = 10;
            page = 1;
            $("#page button:first").text("X10");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        $("#page a:eq(1)").click(function () {
            size = 20;
            page = 1;
            $("#page button:first").text("X20");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        $("#page a:eq(2)").click(function () {
            size = 30;
            page = 1;
            $("#page button:first").text("X30");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        //上一页
        $("#page span:eq(1) button:first").click(function () {
            var lask = Math.ceil(total / size)
            if (page > 1) {
                page = page - 1;
                query();
                if (page > 2 && page < lask - 2) {
                    var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                    for (let i = 0; i < 5; i++) {
                        $($page[i]).text(parseInt($($page[i]).text()) - 1);
                    }
                }
            }
        })
        //下一页
        $("#page span:eq(1) button:last").click(function () {
            var lask = Math.ceil(total / size)
            if (page < lask) {
                page = page + 1;
                query();
                if (page > 3 && page < lask - 1) {
                    var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                    for (let i = 0; i < 5; i++) {
                        $($page[i]).text(parseInt($($page[i]).text()) + 1);
                    }
                }
            }
        })
        //点击跳转
        $("#page span:eq(1) button:gt(0):lt(5)").click(function () {
            page = $(this).text();
            if (page > Math.ceil(total / size)) {
                page = Math.ceil(total / size);
            }
            query();
            page_skip();
        })
        //输入跳转
        $("#page span:eq(2) button").click(function () {
            page = $("#page span:eq(2) input").val();
            if (page > Math.ceil(total / size)) {
                page = Math.ceil(total / size);
            }
            if (page < 1) {
                page = 1;
            }
            query();
            page_skip();
        })

        // 页面跳转下面的数字
        function page_skip() {
            var lask = Math.ceil(total / size);
            if (lask > 5) {
                if (page <= 3) {
                    var count = 1;
                } else if (page >= lask - 2) {
                    var count = lask - 4;
                } else {
                    var count = page - 2;
                }
                var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                for (let i = 0; i < 5; i++) {
                    $($page[i]).text(count++);
                }
            }
        }


    })
})