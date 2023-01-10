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
                        var $chunkElement = $(".chunk-card")[0];
                        var list = data.data;
                        for (const item in list) {
                            var $item = $("<div class='item'></div>").text(item).click(itemfun($(this).text()));
                            var $img = $("<div class='img'></div>");
                            var img2 = "<img src='../../img/alter.png' class='alter'>";
                            $img.append(img2);
                            $item.append($img);




                        }




                    } else {
                        util.hint(data.msg)
                    }
                }
            })
        }



        function itemfun(college) {
            // $(this).
            //todo college框起来
            var $chunkElement = $(".chunk-card")[1];
            $($chunkElement).empty();
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
                        for (const item in list) {
                            var $item = $("<div class='item'></div>").text(item).click(
                                function () {
                                    //todo 点击框起来
                                }
                            );
                            var $img = $("<div class='img'></div>");
                            var img1 = "<img src='../../img/query.png' class='query'>"; //查询，点击显示课程列表
                            var img2 = "<img src='../../img/alter.png' class='alter'>"; //点击修改名称
                            var img3 = "<img src='../../img/del1.png' class='del'>"; //删除

                            $img.append(img1, img2, img3);
                            $item.append($img);
                            $chunkElement.append($item);

                        }
                    } else {
                        util.hint(data.msg);
                    }

                }
            });



        }

    })
})