require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var page = 1;
        var size = 10;
        var total = 0;
        var college = null;
        var careerId = null;
        var careerName = null;
        var className = null;
        const STR = ["college", "careerName", "className", "peopleNum", "courseNum"];
        query();


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
                                window.location.href = "nin-class-course?classId=" + id;
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
                box($select);
            }

            $td.append($select);
            $careerId.append($td);

            //todo 编辑时无默认值

            // $("#careerId").find("option[text="+($(nextAll[1]).text())+"]").attr("selected", true);

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


        function box($select) {
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
                                var $op3 = $("<option></option>").html("&nbsp;&nbsp;" + value[i].careerName).val(value[i].id);
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
            // $("#module", window.parent.document).find("table").append($tr);
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


        /*-------careerCourse----------*/


        $("#quit").click(function () {
            $("#careerCourse").css("display", "none");
            $("button").attr('disabled', false);
        })


        $("#addCourse").click(function () {
            $("#careerCourse").css("display", "block");
            $("button").attr('disabled', true);
            courseCheckbox();
            careerCheckbox();
        })

        $("#newCourse").click(function () {
            $("#oldCourse").css("display", "none");
            courseCheckbox();
        })

        function refresh() {
            careerCheckbox();
        }


        function careerCheckbox() {
            //获取专业列表
            $.ajax({
                url: "nin-career/getCollegeCareerList",
                type: "post",
                dataType: "json",
                success: function (data) {
                    if (data.code == 200) {
                        var img1 = "<img src='../../img/add.jpg' class='add' width='25px' height='25px'>"
                        var img2 = "<img src='../../img/del.png' class='del' width='25px' height='25px'>"
                        var img3 = "<img src='../../img/alter.jpg' class='alter' width='25px' height='25px'>"
                        var img4 = "<img src='../../img/query.jpg' class='query' width='25px' height='25px'>"

                        //生成专业全选框
                        $("#careerCheckbox").empty();
                        var $text = "<input type='checkbox' id='career'><label for='career'><h5>全选</h5></label>";

                        //学院专业复选框
                        $("#careerCheckbox").append($text);
                        for (let key1 in data.data) {
                            if (key1 == "补课") {
                                continue;
                            }
                            var value1 = data.data[key1];
                            var input1 = $("<input type='checkbox' class='career'>").attr("id", key1);
                            var label1 = $("<label for=" + key1 + "><h5>" + key1 + "</h5></label>");
                            var div1 = $("<div></div>").append("<br>", input1, label1, img1)
                            $("#careerCheckbox").append(div1);
                            for (let i = 0; i < value1.length; i++) {
                                var input2 = $("<input type='checkbox' class='career'>").attr("name", key1).attr("id", value1[i].id);
                                var label2 = $("<label for=" + value1[i].id + ">" + value1[i].careerName + "</label>");
                                var div2 = $("<div></div>").append(input2, label2, img2, img3, img4)
                                $("#careerCheckbox").append(div2);
                            }
                            //二级的全选框
                            $("#" + key1).click(function () {
                                if ($("#" + key1).is(':checked')) {
                                    $("input[name=" + key1 + "]").prop('checked', true);
                                } else {
                                    $("input[name=" + key1 + "]").prop('checked', false);
                                }
                            })
                            $("input[name=" + key1 + "]").click(function () {
                                if (!$(this).is(':checked')) {
                                    $("#" + key1).prop('checked', false);
                                }
                            })
                        }
                        //一级的全选框
                        $("#career").click(function () {
                            if ($("#career").is(':checked')) {
                                $("input[class='career']").prop('checked', true);
                            } else {
                                $("input[class='career']").prop('checked', false);
                            }
                        })
                        $("input[class='career']").click(function () {
                            if (!$(this).is(':checked')) {
                                $("#career").prop('checked', false);
                            }
                        })


                        //新增专业
                        $(".add").click(function () {
                            // $("img").unbind();
                            var id = $($(this).parent("div").children("input")[0]).attr("id");
                            // 跳出弹窗，添加
                            var $careerName = $("<tr><td><label for='careerName'>专业:</label></td><td><input type='text' id='careerName'></td></tr>");
                            util.popup([$careerName], ["careerName"], $insert);

                            function $insert(record) {
                                $.ajax({
                                    url: "nin-career/addCareer",
                                    dataType: "json",
                                    type: "post",
                                    data: {
                                        college: id,
                                        careerName: record.careerName
                                    },
                                    success: function (data) {
                                        if (data.code == 200) {
                                            // careerCheckbox();
                                            refresh();
                                        } else {

                                            util.hint(data.msg);
                                        }
                                    }
                                })
                            }
                        })

                        //新学院添加
                        $("#add").click(function () {
                            //添加新学院及专业
                            var $college = $("<tr><td><label for='college'>学院:</label></td><td><input type='text' id='college'></td></tr>");
                            var $careerName = $("<tr><td><label for='careerName'>专业:</label></td><td><input type='text' id='careerName'></td></tr>");
                            util.popup([$college, $careerName], ["college", "careerName"], $insert);

                            function $insert(record) {
                                $.ajax({
                                    url: "nin-career/addCareer",
                                    dataType: "json",
                                    type: "post",
                                    data: {
                                        college: record.college,
                                        careerName: record.careerName
                                    },
                                    success: function (data) {
                                        if (data.code == 200) {
                                            refresh();
                                        } else {
                                            util.hint(data.msg);
                                        }
                                    }
                                })
                            }
                        })

                        //专业删除
                        $(".del").click(function () {
                            var id = $($(this).parent("div").children("input")[0]).attr("id");
                            //根据id删除
                            $.ajax({
                                url: "nin-career/delCareer",
                                type: "post",
                                dataType: "json",
                                data: {
                                    id: id
                                },
                                success: function (data) {
                                    if (data.code == 200) {
                                        refresh();
                                    } else {
                                        util.hint(data.msg);
                                    }
                                }
                            })

                        })

                        //专业学院修改
                        $(".alter").click(function () {
                            var id = $($(this).parent("div").children("input")[0]).attr("id");
                            var $careerName = $("<tr><td><label for='careerName'>专业:</label></td><td><input type='text' id='careerName' value=" + $($(this).parent("div").children()[1]).text() + "></td></tr>");
                            util.popup([$careerName], ["careerName"], $insert);

                            function $insert(record) {
                                if (record.careerName != $($(this).parent("div").children()[1]).text()) {
                                    $.ajax({
                                        url: "nin-career/alterCareer",
                                        dataType: "json",
                                        type: "post",
                                        data: {
                                            id: id,
                                            careerName: record.careerName
                                        },
                                        success: function (data) {
                                            if (data.code == 200) {
                                                refresh();
                                            } else {
                                                util.hint(data.msg);
                                            }
                                        }
                                    })
                                }
                            }

                        })

                        //查看
                        $(".query").click(function () {
                            var id = $($(this).parent("div").children("input")[0]).attr("id");
                            $("#oldCourse").css("display", "block");
                            //根据专业id查询已选课程，并将右侧跳转
                            courseAlready(id);
                        })

                    }
                }
            })
        }

        function courseCheckbox() {
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
                        $("#courseCheckbox").empty();
                        var list = data.data;
                        var $text = "<input type='checkbox' id='course'><label for='course'><h5>全选</h5></label><br>";
                        $("#courseCheckbox").append($text);
                        for (let i = 0; i < list.length; i++) {
                            var input = $("<input type='checkbox' class='course'>").attr("id", list[i].id);
                            var label = $("<label for=" + list[i].id + ">" + list[i].courseName + "</label><br>");
                            $("#courseCheckbox").append(input, label);
                        }

                        //全选事件
                        $("#course").click(function () {
                            if ($("#course").is(':checked')) {
                                $("input[class='course']").prop('checked', true);
                            } else {
                                $("input[class='course']").prop('checked', false);
                            }
                        })
                        $("input[class='course']").click(function () {
                            if (!$(this).is(':checked')) {
                                $("#course").prop('checked', false);
                            }
                        })

                        //确认添加按钮
                        var bu = "<button type='button' class='btn btn-info' id='confirm'>确定添加</button>";
                        $("#courseCheckbox").append(bu);

                        //确定添加事件
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
                    }
                }
            })
        }

        function courseAlready(careerId) {
            $("#courseCheckbox").empty();
            var img2 = "<img src='../../img/del.png' class='del1' width='25px' height='25px'>"
            var img4 = "<img src='../../img/query.jpg' class='query1' width='25px' height='25px'>"
            $.ajax({
                url: "nin-career-course/getSelectList",
                type: "post",
                dataType: "json",
                data: {
                    careerId: careerId
                },
                success: function (data) {
                    if (data.code == 200) {
                        if (data.data.length != 0) {
                            var map = new Map();
                            var list = data.data;
                            for (let i = 0; i < list.length; i++) {
                                var str = "课程名称：" + list[i]["courseName"] +
                                    "\n教室类型：" + list[i]["houseType"] +
                                    "\n" + list[i]["must"] + " " + list[i]["courseTime"] +
                                    "课时\n开课时间：第" + list[i]["startTime"] +
                                    "周\n结课时间：第" + list[i]["endTime"] +
                                    "周\n上课周次：" + list[i]["weekTime"] + "周";
                                map.set(list[i]["id"], str);
                                // $(img4).
                                var div = $("<div>" + list[i]["courseName"] + "</div>").attr("id", list[i]["id"]).append(img2, img4);
                                $("#courseCheckbox").append(div)
                            }
                        } else {
                            $("#courseCheckbox").text("暂无课程");
                        }
                    }
                    $(".del1").click(function () {
                        var id = $(this).parent("div").attr("id");
                        $.ajax({
                            url: "nin-career-course/delCareerCourse",
                            type: "post",
                            dataType: "json",
                            data: {
                                id: id
                            },
                            success: function (data) {
                                if (data.code == 200) {
                                    courseAlready(careerId);
                                } else {
                                    util.hint(data.msg);
                                }
                            }
                        })

                    })

                    $(".query1").click(function () {
                        var id = $(this).parent("div").attr("id");
                        alert(map.get(id));

                    })

                }
            })


        }


        /**/

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