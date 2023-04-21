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
        var careerId = null;
        var classId = null;
        var teacherName = null;
        var courseName = null;
        var houseName = null;
        var week = null;
        var pitchNum = null;
        query();
        const STR = ["careerName", "className", "teacherName", "courseName", "houseName", "cnMust", "cnWeekly", "startTime", "endTime", "cnWeek", "cnPitchNum"];

        util.navPath('/排课管理/排课信息管理');

        //排课
        $("#arrange").click(function (){
            $.ajax({
                url: "nin-arrange/arrange",
                dataType:"json",
                type: "get",
                success:function (data){
                    if (data.code == 200) {
                        query();
                        util.hint("生成成功！");
                    } else {
                        util.hint(data.msg);
                    }

                }
            })
        })

        //清空
        $("#empty").click(function () {
            $.ajax({
                url: "nin-arrange/empty",
                dataType:"json",
                type: "get",
                success:function (){
                    query();
                    util.hint("删除成功！");
                }
            })
        })

        //专业选择下拉框
        $("#dropupCareerIdButton").click(function () {
            //重置班级选择
            classId = null;
            $("#dropupClassIdButton").text("班级").removeAttr("class-id");

            $.ajax({
                url: "nin-career/getCareerList",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupCareerIdButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            if ( list[i].id == -1) {
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

            careerId = $("#dropupCareerIdButton").attr("career-id");

            $.ajax({
                url: "nin-class/getClassList",
                dataType: "json",
                type: "get",
                data: {
                    careerId: careerId
                },
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        $("#dropupClassIdButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i].className).attr("class-id", list[i].id).click(function () {
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
            careerId = $("#dropupCareerIdButton").attr("career-id");
            classId = $("#dropupClassIdButton").attr("class-id");
            teacherName = $("#teacherName").val();
            houseName = $("#houseName").val();
            courseName = $("#courseName").val();
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            careerId = null;
            $("#dropupCareerIdButton").text("专业").removeAttr("career-id");
            classId = null;
            $("#dropupClassIdButton").text("班级").removeAttr("class-id");
            teacherName = null;
            $("#teacherName").val("");
            houseName = null;
            $("#houseName").val("");
            courseName = null;
            $("#courseName").val("");
            week = null;
            $("#week").val("");
            pitchNum = null;
            $("#pitchNum").val("");
            query();
        })


        //星期节数下拉
        $("#week").change(function(){
            week = $(this).val();
            query();
        });
        $("#pitchNum").change(function(){
            pitchNum = $(this).val();
            query();
        });

        //查询
        function query() {
            $.ajax({
                url: "nin-arrange/getPageSelectList",
                dataType: "json",
                type: "get",
                data: {
                    careerId: careerId,
                    classId: classId,
                    teacherName: teacherName,
                    courseName: courseName,
                    houseName: houseName,
                    week: week,
                    pitchNum: pitchNum,
                    size: size,
                    page: page
                },
                success: function (data) {
                    if (data.code == 200) {
                        total = data.data.total;
                        var isOk = data.data.isOk;//是否排课过
                        if ((data.data.list).length != 0) {
                            $("#page span:eq(2) input").val(page);
                            var count = 4;
                            if (!isOk) {
                                count = 5;
                            }
                            util.createForm((page - 1) * size + 1, data.data.list, STR, count);
                            $("#page-text").text("共" + total + "条数据, " + Math.ceil(total / size) + "页");


                            $(".delete").click(function () {
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                del(id);
                            })
                            $(".update").click(function () {
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                alter(id);
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
                url: "nin-arrange/delArrange",
                dataType: "json",
                type: "post",
                data: {
                    id: id
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

        //编辑
        function alter(id) {
            $.ajax({
                url: "nin-arrange/getHouseByArrangeId",
                type: "get",
                dataType: "json",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        var len = list.length;

                        var $houseId = $("<tr><td><label for='houseId'>教室:</label></td></tr>");
                        var $td = $("<td></td>")
                        var $select = $("<select id='houseId'></select>");
                        for (let i = 0; i < len; i++) {
                            var $op = $("<option></option>").text(list[i].houseName).attr("value", list[i].id);
                            $select.append($op);
                        }
                        $td.append($select);
                        $houseId.append($td);

                        var $week =
                            "<tr>" +
                            "<td>" +
                            "<label for='week'>星期:</label>" +
                            "</td>" +
                            "<td>" +
                            "<select id='week'>" +
                            "<option value='1'>星期一</option>" +
                            "<option value='2'>星期二</option>" +
                            "<option value='3'>星期三</option>" +
                            "<option value='4'>星期四</option>" +
                            "<option value='5'>星期五</option>" +
                            "<option value='6'>星期六</option>" +
                            "<option value='7'>星期日</option>" +
                            "</select>" +
                            "</td>" +
                            "</tr>";

                        var $pitchNum =
                            "<tr>" +
                            "<td>" +
                            "<label for='week'>节数:</label>" +
                            "</td>" +
                            "<td>" +
                            "<select id='pitchNum'>" +
                            "<option value='1'>第一节</option>" +
                            "<option value='2'>第二节</option>" +
                            "<option value='3'>第三节</option>" +
                            "<option value='4'>第四节</option>" +
                            "<option value='5'>第五节</option>" +
                            "</select>" +
                            "</td>" +
                            "</tr>";


                        util.popup([$houseId, $week, $pitchNum], ["houseId", "week", "pitchNum"], $update);

                        function $update(record) {
                            $.ajax({
                                url: "nin-arrange/alterArrange",
                                dataType: "json",
                                type: "post",
                                data: {
                                    id: id,
                                    houseId: record.houseId,
                                    week: record.week,
                                    pitchNum: record.pitchNum
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