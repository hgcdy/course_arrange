require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        var college = null;
        var careerIdList = "[]";

        query();

        // 获取数据
        function query() {
            //学院
            itemCollege();
            //所有课程
            itemCourseAll();
            //课程框


            $(".chunk-card-head div").click(function () {
                switchover($(this));
            });
            //所有课程
            $(".chunk-card-head div:first").click(function () {
                itemCourseAll();
            });
            $(".chunk-card-head div:last").click(function () {
                $(".chunk-card-body:last").empty();
            });

        }

        //item学院
        function itemCollege() {
            $(".chunk-card-body:first").empty();

            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var $chunkElement = $(".chunk-card-body")[0];
                        var list = data.data;
                        var length = list.length;
                        for (let i = 0; i < length; i++) {
                            var item = list[i];
                            var $item = $("<div class='item'></div>").click(function (){
                                itemCareer($(this));
                            });

                            var $text = $("<div class='text'></div>").text(item);
                            var $img = $("<div class='img'></div>");
                            var img2 = $("<img src='../../img/alter.png' class='alter'>").attr("data-college", item);//todo 编辑
                            $img.append(img2);
                            $item.append($text, $img);
                            $($chunkElement).append($item);
                        }

                        $(".chunk-card-body:first .alter").click(function () {
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
        }

        //item专业
        function itemCareer($college) {
            var $1 = $($college);

            var $chunkElement = $(".chunk-card-body")[1];
            $($chunkElement).empty();
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
                                var img1 = "<img src='../../img/query.jpg' class='query'>"; //查询，点击显示课程列表
                                var img2 = "<img src='../../img/alter.png' class='alter'>"; //点击修改名称
                                var img3 = "<img src='../../img/del.png' class='del'>"; //删除

                                $img.append(img1, img2, img3);
                                $item.append($text, $img);
                                $($chunkElement).append($item);

                            }
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
        
        
        //课程框切换
        function switchover(obj) {
            $(".chunk-card-head div").css("border", "");
            $(obj).css("border", "1px solid #1dc072");
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