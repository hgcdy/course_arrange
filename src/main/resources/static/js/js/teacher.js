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
        var teacherName = null;
        const STR = ["teacherName", "teacherCode", "courseName"];
        query();
        util.navPath("/用户管理/教师管理");

        //查询按钮
        $("#query").click(function () {
            teacherName = $("#teacherName").val();
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            teacherName = null;
            $("#teacherName").val(null);
            query();
        })

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-teacher/getPageSelectList",
                dataType: "json",
                type: "get",
                data: {
                    teacherName: teacherName,
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
                            $(".details").click(function (){
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                util.setCache("detailId", id);
                                util.setCache("type", "teacher");
                                window.location.href = "nin-teacher-course?token=" + util.getToken();
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
                url: "nin-teacher/delTeacher",
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
                url: "nin-teacher/getTeacherById",
                dataType: "json",
                type: "get",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var obj = data.data;
                        var $teacherName = $("<tr><td><label for='teacherName'>教师名称:</label></td><td><input type='text' id='teacherName' value=" + obj.teacherName + "></td></tr>");
                        var $teacherCode = $("<tr><td><label for='teacherCode'>教师账号:</label></td><td><input type='text' id='teacherCode' value=" + obj.teacherCode + "></td></tr>");
                        var $teacherPassword = $("<tr><td><label for='teacherPassword'>密码:</label></td><td><input type='text' id='teacherPassword' value='******'></td></tr>");
                        util.popup([$teacherName, $teacherCode, $teacherPassword], ["teacherName", "teacherCode", "teacherPassword"], $update);
                    }
                }
            })

            function $update(record) {
                if (record.teacherPassword == "******") {
                    record.teacherPassword = null;
                }

                $.ajax({
                    url: "nin-teacher/alterTeacher",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id,
                        teacherName: record.teacherName,
                        teacherCode: record.teacherCode,
                        teacherPassword: record.teacherPassword
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
            var $teacherName = $("<tr><td><label for='teacherName'>教师名称:</label></td><td><input type='text' id='teacherName'></td></tr>");
            var $teacherCode = $("<tr><td><label for='teacherCode'>教师账号:</label></td><td><input type='text' id='teacherCode'></td></tr>");
            var $teacherPassword = $("<tr><td><label for='teacherPassword'>密码:</label></td><td><input type='text' id='teacherPassword'></td></tr>");
            util.popup([$teacherName, $teacherCode, $teacherPassword], ["teacherName", "teacherCode", "teacherPassword"], $insert);
            function $insert(record) {

                    $.ajax({
                        url: "nin-teacher/addTeacher",
                        dataType: "json",
                        type: "post",
                        data: {
                            teacherName: record.teacherName,
                            teacherCode: record.teacherCode,
                            teacherPassword: record.teacherPassword
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