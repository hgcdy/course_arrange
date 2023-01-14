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
        var careerId = null;
        var className = null;
        const STR = ["college", "careerName", "className", "peopleNum", "courseNum"];
        query();
        util.navPath('/用户管理/班级管理');


        //学院选择下拉框
        $("#dropupCollegeButton").click(function () {
            //重置专业选择
            careerId = null;
            $("#dropupCareerButton").text("专业").removeAttr("career-id");
            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupCollegeButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
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
        $("#dropupCareerButton").click(function () {
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院") {
                college = null;
            }

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
                        $("#dropupCareerButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            if (list[i].id == -1) {
                                continue;
                            }
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i].careerName).attr("career-id", list[i].id).click(function () {
                                $("#dropupCareerButton").text($(this).text()).attr("career-id", $(this).attr("career-id"));
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupCareerButton").next("ul").append($li);
                        }
                    }
                }
            })
        })


        //查询按钮
        $("#query").click(function () {
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院") {
                college = null;
            }
            careerId = $("#dropupCareerButton").attr("career-id");
            className = $("#className").val();
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            college = null;
            $("#dropupCollegeButton").text("学院");
            careerId = null;
            $("#dropupCareerButton").text("专业").removeAttr("career-id");
            className = null;
            $("#className").val(null);
            query();
        })

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-class/getPageSelectList",
                dataType: "json",
                type: "post",
                data: {
                    college: college,
                    careerId: careerId,
                    className: className,
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
                                util.setDetailInfo(id, "class");
                                window.location.href = "nin-class-course?token=" + util.getToken();
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


        //新增
        $("#insert").click(function () {
            var $className = $("<tr><td><label for='className'>班级名称:</label></td><td><input type='text' id='className'></td></tr>");
            var $careerId = $("<tr><td><label for='careerId'>专业:</label></td></tr>");
            var $td = $("<td></td>")
            var $select = $("<select id='careerId'></select>");
            var $op1 = $("<option disabled='disabled' selected='selected'></option>");
            $($select).append($op1);
            box($select);
            $td.append($select);
            $careerId.append($td);

            util.popup([$careerId, $className], ["careerId", "className"], $insert);

            function $insert(record) {
                $.ajax({
                    url: "nin-class/addClass",
                    dataType: "json",
                    type: "post",
                    data: {
                        careerId: record.careerId,
                        className: record.className
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

        //编辑
        function alter($id) {
            var id = $($id).attr("data-id");
            var nextAll = $($id).nextAll();
            var $className = $("<tr><td><label for='className'>班级名称:</label></td><td><input type='text' id='className' value=" + $(nextAll[2]).text() + "></td></tr>");
            var $careerId = $("<tr><td><label for='careerId'>专业:</label></td></tr>");
            var $td = $("<td></td>")
            var $select = $("<select id='careerId'></select>");

            if ($(nextAll[1]).text() == "选修") {
                var $op = $("<option id='0'>选修</option>");
                $($select).append($op);
            } else {
                var $op1 = $("<option disabled='disabled' selected='selected'></option>");
                $($select).append($op1);
                box($select, $(nextAll[1]).text());
            }

            $td.append($select);
            $careerId.append($td);

            util.popup([$careerId, $className], ["careerId", "className"], $update);

            function $update(record) {
                $.ajax({
                    url: "nin-class/alterClass",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id,
                        careerId: record.careerId,
                        className: record.className
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


        function box($select, careerName_) {
            $.ajax({
                url: "nin-career/getCollegeCareerList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        for (let key in data.data) {
                            if (key == "补课") {
                                continue;
                            }
                            var value = data.data[key];
                            var $op2 = $("<option disabled='disabled'></option>").html("&nbsp;" + key);
                            $($select).append($op2);

                            for (let i = 0; i < value.length; i++) {
                                if (careerName_ != null && careerName_ == value[i].careerName) {
                                    var $op3 = $("<option selected = 'selected'></option>").html("&nbsp;&nbsp;" + value[i].careerName).val(value[i].id);
                                } else {
                                    var $op3 = $("<option></option>").html("&nbsp;&nbsp;" + value[i].careerName).val(value[i].id);
                                }
                                $($select).append($op3);
                            }


                        }
                    }
                }
            })
        }


        //删除
        function del(id) {
            var $tr = $("<tr><td colspan='2'>删除班级会将与该班级有关的所有信息一起删除</td>></tr>");
            util.popup([$tr], [], function () {

                $.ajax({
                    url: "nin-class/delClass",
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
            });

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