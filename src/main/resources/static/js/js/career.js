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
                                itemfun($(this));
                            });

                            var $text = $("<div class='text'></div>").text(item);
                            var $img = $("<div class='img'></div>");
                            var img2 = $("<img src='../../img/alter.png' class='alter'>");//todo 编辑
                            $img.append(img2);
                            $item.append($text, $img);
                            $($chunkElement).append($item);
                        }
                    } else {
                        util.hint(data.msg)
                    }
                }
            })
        }

        function itemfun($college) {
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
                                console.log(item);
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

        function unoptAll(obj) {
            $(obj).parent().children().css("border", "1px solid #dddddd").attr("data-opt", null).click(function () {
                opt($(this), 0);
            })
        }


    })
})