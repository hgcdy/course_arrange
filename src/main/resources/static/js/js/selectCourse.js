require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        var role = util.getCache("role");
        var detailId = util.getCache("detailId");
        var type = util.getCache("type");

        if (role === "admin") {
            if (type === "class") {
                util.navPath("/用户管理/班级管理/详情");
            } else if (type === "teacher") {
                util.navPath("/用户管理/教师管理/详情");
            } else if (type === "student") {
                util.navPath("/用户管理/学生管理/详情");
            }
        }

        var url;
        if (type !== "class") {
            url = "nin-" + type + "-course";
        } else {
            url = "nin-" + type;
        }


        if (role === "admin") {
            var bu1 = '<button type="button" class="btn" id="back">返回</button>';
            var bu2 = '<button type="button" class="btn disabled" id="details">选课</button>';
            var bu3 = '<button type="button" class="btn" id="formButton">课程表</button>';
            $("#bottom-head").append(bu1, bu2, bu3);
        }

        query();

        function query() {
            $(".chunk-card-body:lt(2)").empty();

            $.ajax({
                url: url + "/getCourse",
                dataType: "json",
                type: "post",
                data: {
                    id: detailId
                },
                success: function (data) {
                    if (data.code === 200) {
                        var unselected = data.data.unselected;
                        for (const key in unselected) {
                            addItem($(".chunk-card-body:eq(0)"), unselected[key], 1);
                        }
                        var selected = data.data.selected;
                        for (const key in selected) {
                            addItem($(".chunk-card-body:eq(1)"), selected[key], -1);
                        }

                        $(".item").click(function () {
                            $(".item").css("border", "");
                            $(this).css("border", "2px solid #1dc072");
                            var id = $(this).attr("data-id");
                            $.ajax({
                                url: "nin-course/getCourseAndState",
                                dataType: "json",
                                type: "get",
                                data: {
                                    id: id,
                                    userType: type
                                },
                                success: function (data) {
                                    if (data.code === 200) {
                                        var $td = $("td");
                                        var record = data.data;

                                        $($td[1]).text(record.courseName);
                                        $($td[3]).text(record.cnHouseType);
                                        $($td[5]).text(record.courseTime);
                                        $($td[7]).text(record.cnMust);
                                        $($td[9]).text(record.startTime + "-" + record.endTime);
                                        $($td[11]).text(record.start);
                                    } else {
                                        util.hint(data.msg);
                                    }
                                    
                                }
                            })
                        })

                        $(".add").click(function () {
                            //ajax添加
                            fun($(this).parent().attr("data-id"), "/addCourse");
                        })

                        $(".del").click(function () {
                            //ajax删除
                            fun($(this).parent().attr("data-id"), "/delCourse");
                        })



                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }

        function fun(courseId, path) {
            $.ajax({
                url: url + path,
                dataType: "json",
                type: "post",
                data: {
                    id: detailId,
                    courseId: courseId
                },
                success: function (data) {
                    if (data.code === 200) {
                        query();
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }


        /**
         * 添加item
         * sign 1-新增， -1-删除
         */
        function addItem(obj, itemInfo, sign) {
            var id = itemInfo.id;
            var name = itemInfo.name;
            var isOk = itemInfo.isOk;
            var $item = $("<div class='item'></div>").attr("data-id", id);
            var $text = $("<div class='text'></div>").text(name);
            if (isOk) {
                if (sign === 1) {
                    var $add = "<div class='add'><a href='javaScript:void(0)'>添加</a></div>";
                    $item.append($text, $add);
                } else if (sign === -1) {
                    var $del = "<div class='del'><a href='javaScript:void(0)'>删除</a></div>";
                    $item.append($text, $del);
                }
            } else {
                $item.append($text);
            }
            $(obj).append($item);
        }



        //返回
        $("#back").click(function (){
            window.location.href = "nin-" + type + "?token=" + util.getToken();
        })

        $("#details").click(function (){
            window.location.href = window.location.href;
        })

        //课程表
        $("#formButton").click(function (){
            window.location.href = "nin-arrange/courseForm?token=" + util.getToken();
        })
    })
})