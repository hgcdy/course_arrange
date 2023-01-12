require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        query();

        //所有课程按钮
        $(".chunk-card-head div:first").click(function () {
            $(".chunk-card-head div:last").css("display", "none");
            $(".chunk-card-head div").css("border", "");
            $(this).css("border", "1px solid #1dc072");
            itemCourseAll();
        });

        //新增专业
        $(".chunk-card-foot:eq(1)").children("button").click(function () {
            var val = $chunk.children("input").val();
            if (val == "") {
                util.hint("请勿置空");
            } else {

                $.ajax({
                    url: "nin-career/addCareer",
                    dataType: "json",
                    type: "post",
                    data: {
                        college: getCollege(),
                        careerName: val
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            itemCollege();
                        } else {
                            util.hint(data.msg);
                        }
                    }
                })
            }
        })

        //专业添加课程，批量
        $("#add-course").click(function () {
            var careerIdList = getCareerIdList();
            var courseIdList = getCourseIdList();
            if (careerIdList.length === 2) {
                util.hint("请选择专业");
                return;
            }
            if (courseIdList.length === 2) {
                util.hint("请选择课程");
                return;
            }
            $.ajax({
                url: "/nin-career-course/addBatchCourse",
                type: "post",
                dataType: "json",
                data: {
                    careerIds: careerIdList,
                    courseIds: courseIdList
                },
                success: function (data) {
                    util.hint(data.msg);
                }
            })



        })

        // 获取数据
        function query() {
            //学院
            itemCollege();
            //所有课程
            itemCourseAll();
            //清除专业
            $(".chunk-card-body:eq(1)").empty();
        }

        //item学院
        function itemCollege() {
            var $chunk1 = $(".chunk-card-body:first");
            $chunk1.empty();

            //显示内容
            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {

                        var list = data.data;
                        var length = list.length;
                        for (let i = 0; i < length; i++) {
                            var item = list[i];
                            addItem($chunk1, item);
                        }

                        //学院修改
                        $(".chunk-card-body:first .alter_1").click(function () {
                            var oldCollege = $(this).attr("data-college");
                            var $college = $("<tr><td><label for='college'>学院名称:</label></td><td><input type='text' id='college' value=" + oldCollege + "></td></tr>");
                            util.popup([$college], ["college"], function (record) {
                                $.ajax({
                                    url: "nin-career/alterCollege",
                                    dataType: "json",
                                    type: "post",
                                    data: {
                                        oldCollege: oldCollege,
                                        newCollege: record.college
                                    },
                                    success: function (data) {
                                        if (data.code == 200) {
                                            itemCollege();
                                        } else {
                                            util.hint(data.msg);
                                        }
                                    }
                                })
                            });
                        })

                    } else {
                        util.hint(data.msg)
                    }
                }
            })

            //新增
            var $chunk = $(".chunk-card-foot:first");
            $chunk.children("button").click(function () {
                var val = $chunk.children("input").val();
                if (val == "") {
                    util.hint("请勿置空");
                } else {
                    addItem($chunk1, val);
                    $chunk.children("input").val("");
                }

            })

            //生成item块
            function addItem($chunk1, item) {
                var $item = $("<div class='item'></div>").click(function (){
                    itemCareer($(this));
                });

                var $text = $("<div class='text'></div>").text(item);
                var $img = $("<div class='img'></div>");
                var img2 = $("<img src='../../img/alter.png' class='alter_1'>").attr("data-college", item);
                $img.append(img2);
                $item.append($text, $img);
                $($chunk1).append($item);
            }
        }

        //item专业
        function itemCareer($college) {
            var $1 = $($college);

            var $chunkElement = $(".chunk-card-body:eq(1)")
            $chunkElement.empty();
            if ($1.attr("data-opt") == null) {
                $.ajax({
                    url: "nin-career/getCareerList",
                    dataType: "json",
                    type: "post",
                    data: {
                        college: $1.text()
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            var list = data.data;

                            var length = list.length;
                            for (let i = 0; i < length; i++) {
                                var item = list[i]["careerName"];

                                var $item = $("<div class='item'></div>").attr("data-id", list[i]["id"]);
                                unopt($item, 1);
                                var $img = $("<div class='img'></div>");

                                var $text = $("<div class='text'></div>").text(item);
                                var img1 = $("<img src='../../img/query.jpg' class='query'>").attr("data-career", item); //查询，点击显示课程列表
                                var img2 = $("<img src='../../img/alter.png' class='alter'>").attr("data-career", item); //点击修改名称
                                var img3 = $("<img src='../../img/del.png' class='del'>").attr("data-career", item); //删除

                                $img.append(img1, img2, img3);
                                $item.append($text, $img);
                                $chunkElement.append($item);
                            }

                            //专业课程查询
                            $(".chunk-card-body:eq(1) .query").click(function () {
                                var $chunk = $(".chunk-card-body:last");
                                $chunk.empty();
                                var parent2 = $(this).parent().parent();
                                var careerId = parent2.attr("data-id");
                                var careerName = parent2.children(".text").text();
                                $.ajax({
                                    url: "nin-career-course/getSelectList",
                                    type: "post",
                                    dataType: "json",
                                    data: {
                                        careerId: careerId
                                    },
                                    success: function (data) {
                                        if (data.code == 200) {
                                            var list = data.data;
                                            for (let i = 0; i < list.length; i++) {
                                                var item = list[i]["courseName"];

                                                var $item = $("<div class='item'></div>").attr("data-id", list[i]["id"]);

                                                var $img = $("<div class='img'></div>");
                                                var $text = $("<div class='text'></div>").text(item);
                                                var img3 = $("<img src='../../img/del.png' class='del'>").attr("data-career", item); //删除

                                                $img.append(img3);
                                                $item.append($text, $img);
                                                $chunk.append($item);
                                            }

                                            $(".chunk-card-head div").css("border", "");
                                            $(".chunk-card-head div:last").text(careerName + "已选课程").css("display", "block").css("border", "1px solid #1dc072");

                                            //专业选择课程删除
                                            $(".chunk-card-body:last .del").click(function () {
                                                var parent = $(this).parent().parent();
                                                var id = parent.attr("data-id");
                                                $.ajax({
                                                    url: "nin-career-course/delCareerCourse",
                                                    type: "post",
                                                    dataType: "json",
                                                    data: {
                                                        id: id
                                                    },
                                                    success: function (data) {
                                                        if (data.code == 200) {
                                                            parent.remove();
                                                        } else {
                                                            util.hint(data.msg);
                                                        }
                                                    }
                                                })

                                            })


                                        } else {
                                            util.hint(data.msg);
                                        }
                                    }
                                })

                            })

                            //专业修改
                            $(".chunk-card-body:eq(1) .alter").click(function () {
                                var $2 = $(this);
                                var careerName = $2.attr("data-career");
                                var parent1 = $2.parent().parent();
                                var careerId = parent1.attr("data-id");
                                var $career = $("<tr><td><label for='careerName'>专业名称:</label></td><td><input type='text' id='careerName' value=" + careerName + "></td></tr>");
                                util.popup([$career], ["careerName"], function (record) {
                                    $.ajax({
                                        url: "nin-career/alterCareer",
                                        dataType: "json",
                                        type: "post",
                                        data: {
                                            id: careerId,
                                            careerName: record.careerName
                                        },
                                        success: function (data) {
                                            if (data.code == 200) {
                                                $2.attr("data-career", record.careerName);
                                                parent1.children(".text").text(record.careerName);
                                            } else {
                                                util.hint(data.msg);
                                            }
                                        }
                                    })
                                });
                            })

                            //专业删除
                            $(".chunk-card-body:eq(1) .del").click(function () {
                                var parent3 = $(this).parent().parent();
                                var careerId = parent3.attr("data-id");
                                $.ajax({
                                    url: "nin-career/delCareer",
                                    type: "post",
                                    dataType: "json",
                                    data: {
                                        id: careerId
                                    },
                                    success: function (data) {
                                        if (data.code == 200) {
                                            parent3.remove();
                                        } else {
                                            util.hint(data.msg);
                                        }
                                    }
                                })
                            })

                        } else {
                            util.hint(data.msg);
                        }

                    }
                });
            }

            //学院选中
            opt($college, 0);


        }

        //查询专业下的课程
        function itemCourse(careerId) {
            var $chunkElement = $(".chunk-card-body:last");
            $chunkElement.empty();

            $.ajax({
                url: "nin-career-course/getSelectList",
                type: "post",
                dataType: "json",
                data: {
                    careerId: careerId
                },
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        for (let i = 0; i < list.length; i++) {
                            var item = list[i]["courseName"];

                            var $item = $("<div class='item'></div>");
                            var $img = $("<div class='img'></div>");

                            var $text = $("<div class='text'></div>").text(item);
                            var img3 = "<img src='../../img/del.png' class='del'>"; //删除

                            $img.append(img3);
                            $item.append($text, $img);
                            $chunkElement.append($item);
                        }

                    } else {
                        util.hint(data.msg);
                    }
                }
            })

        }

        //查询所有课程（准备添加课程）
        function itemCourseAll() {
            var $chunkElement = $(".chunk-card-body")[2];
            $($chunkElement).empty();

            $.ajax({
                url: "nin-course/getSelectCourseList",
                type: "post",
                dataType: "json",
                data: {
                    sign: 1
                },
                success: function (data) {
                    if (data.code == 200) {
                        if (data.data.length != 0) {
                            var list = data.data;
                            var length = list.length;
                            for (let i = 0; i < length; i++) {
                                var item = list[i]["courseName"];
                                var $item = $("<div class='item'></div>").attr("data-id", list[i]["id"]);
                                unopt($item, 1);
                                var $text = $("<div class='text'></div>").text(item);
                                $item.append($text);
                                $($chunkElement).append($item);
                            }
                        }
                    } else {
                        util.hint(data.msg)
                    }
                }
            })

        }

        //获取被选中的学院
        function getCollege() {
            var children = $(".chunk-card-body:first").children("[data-opt='1']");
            return children.text();
        }

        //获取被选中的专业列表
        function getCareerIdList() {
            var children = $(".chunk-card-body:eq(1)").children("[data-opt='1']");
            var len = children.length;
            var careerIdList = "[";
            for (let i = 0; i < len; i++) {
                var id = $(children[i]).attr("data-id");
                careerIdList = careerIdList + id;
                if (i !== len - 1) {
                    careerIdList = careerIdList + ",";
                }
            }
            careerIdList = careerIdList + "]";
            return careerIdList;
        }


        //获取被选中的课程列表
        function getCourseIdList() {
            var children = $(".chunk-card-body:last").children("[data-opt='1']");
            var len = children.length;
            var courseIdList = "[";
            for (let i = 0; i < len; i++) {
                var id = $(children[i]).attr("data-id");
                courseIdList = courseIdList + id;
                if (i !== len - 1) {
                    courseIdList = courseIdList + ",";
                }
            }
            courseIdList = courseIdList + "]";
            return courseIdList;
        }

        
        //选中
        function opt(obj, sign) {
            if (sign === 0) {
                unoptAll(obj);
            }
            $(obj).css("border", "1px solid #1dc072").attr("data-opt", 1).click(function () {
                unopt($(this), sign);
            })
        }

        //取消
        function unopt(obj, sign) {
            $(obj).css("border", "1px solid #dddddd").attr("data-opt", null).click(function () {
                opt($(this), sign);
            })
        }

        //全部取消
        function unoptAll(obj) {
            $(obj).parent().children().css("border", "1px solid #dddddd").attr("data-opt", null).click(function () {
                opt($(this), 0);
            })
        }


    })
})