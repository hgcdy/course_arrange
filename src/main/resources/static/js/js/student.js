require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var page = 1;
        var size = 10;
        var total = 0;
        var studentName = null;
        var career = null;
        var classId = null;
        const STR = ["studentName", "studentCode", "studentPassword", "className"];
        query();

        //班级选择下拉框
        $("#dropupCareerClassButton").click(function () {
            $.ajax({
                url: "nin-class/careerClassList",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        // var map = data.data;
                        $("#dropupCareerClassButton").next("ul").empty();
                        var count = 0
                        for (let key in data.data) {
                            var value = data.data[key];
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(key).attr("data-career", key).click(function () {
                                $("#dropupCareerClassButton").text($(this).text()).attr("data-career", $(this).attr("data-career"));
                            });
                            var $li = $("<li></li>").append($a);

                            var $ul = $("<ul class='dropdown-menu' style='left: 100%;'></ul>").css("top", (-1 + 32 * count++) + "px").attr("data-career", key);
                            for (let i = 0; i < value.length; i++) {
                                var $a1 = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").attr("data-code", value[i].id).text(value[i].className).click(function () {
                                    var prev = $(this).parent().parent().prev();
                                    $("#dropupCareerClassButton").text(prev.text() + "/" + $(this).text()).attr("data-career", prev.text()).attr("data-code", $(this).attr("data-code"));
                                })
                                var $li1 = $("<li></li>").append($a1);
                                $ul.append($li1);
                            }

                            $li.mouseover(function () {
                                $(this).next("ul").css("display", "block");
                            })
                            $li.mouseout(function () {
                                $(this).next("ul").css("display", "none");
                            })
                            $ul.mouseover(function () {
                                $(this).css("display", "block");
                            })
                            $ul.mouseout(function () {
                                $(this).css("display", "none");
                            })

                            $("#dropupCareerClassButton").next("ul").append($li, $ul);
                        }


                    }
                }
            })
        })
        //查询按钮
        $("#query").click(function () {
            studentName = $("#studentName").val();
            career = $("#dropupCareerClassButton").attr("data-career");
            classId = $("#dropupCareerClassButton").attr("data-code");
            if ($.trim($("#dropupCareerClassButton").text()) == "专业班级") {
                career = null;
                classId = null;
            }
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            studentName = null;
            $("#studentName").val(null);
            career = null;
            classId = null;
            $("#dropupCareerClassButton").text("专业班级").attr("data-career", "").attr("data-code", "");
            query();
        })


        function query() {
            $.ajax({
                url: "nin-student/getPageSelectList",
                dataType: "json",
                type: "post",
                data: {
                    studentName: studentName,
                    career: career,
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
                                window.location.href = "nin-student-course?studentId=" + id;
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
                url: "nin-student/getStudentById",//todo
                dataType: "json",
                type: "post",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var obj = data.data;
                        var $studentName = $("<tr><td><label for='studentName'>学生名称:</label></td><td><input type='text' id='studentName' value=" + obj.studentName + "></td></tr>");
                        var $studentCode = $("<tr><td><label for='studentCode'>学生账号:</label></td><td><input type='text' id='studentCode' value=" + obj.studentCode + "></td></tr>");
                        var $studentPassword = $("<tr><td><label for='studentPassword'>密码:</label></td><td><input type='text' id='studentPassword' value=" + obj.studentPassword + "></td></tr>");
                        var $classId = $("<tr><td><label for='classId'>班级:</label></td></tr>");
                        var $td = $("<td></td>")
                        var $select = $("<select id='classId'></select>");
                        box($select);
                        $td.append($select);
                        $classId.append($td);

                        util.popup([$studentName, $studentCode, $studentPassword, $classId], ["studentName", "studentCode", "studentPassword", "classId"], $update);
                    }
                }
            })

            function $update(record) {
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

        function box($select) {
            $.ajax({
                url: "nin-class/careerClassList",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        for (let key in data.data) {
                            if (key == "#"){
                                continue;
                            }
                            var value = data.data[key];
                            var $op2 = $("<option disabled='disabled'></option>").html("&nbsp;" + key);
                            $($select).append($op2);
                            for (let i = 0; i < value.length; i++) {
                                var $op3 = $("<option></option>").html("&nbsp;&nbsp;" + value[i].className).val(value[i].id);
                                $($select).append($op3);
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