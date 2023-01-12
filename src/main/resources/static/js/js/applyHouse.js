require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        util.navPath('/排课管理/教室申请');

        //选择框切换
        $("#apply-nav div").click(function () {
            $("#apply-nav div").css("border", "");
            $(this).css("border", "1px solid #1dc072");
            var attr = $(this).attr("id");
            switch(attr) {
                case "nav-class":
                    getClass();
                    break;
                case "nav-house":
                    getHouse();
                    break;
                case "nav-teacher":
                    getTeacher();
                    break;
                case "nav-course":
                    getCourse();
                    break;
                case "nav-time":
                    getTime();
                    break;
            }
        })

        function getClass() {
            var $apply = $(".apply-body:first");
            $apply.empty();
            $.ajax({
                url: "nin-career/getCollegeCareerList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var items = $("<div class='items'></div>").text("--学院专业--");
                        $apply.append(items);
                        var map = data.data;
                        for (const key in map) {
                            var items1 = $("<div class='items'></div>").text(key);
                            $apply.append(items1);
                            var list = map[key];
                            var len = list.length;

                            var items2 = $("<div class='items'></div>");
                            for (let i = 0; i < len; i++) {
                                var id = list[i].id;
                                var careerName = list[i].careerName;
                                var item = $("<div class='item'></div>").text(careerName).attr("data-id", id);
                                items2.append(item);
                                if ((i + 1) % 6 === 0 || i === len - 1) {
                                    $apply.append(items2);
                                    items2 = $("<div class='items'></div>");
                                }
                            }
                        }

                        //点击专业展示班级
                        $(".apply-body:first").find(".item").click(function () {
                            var careerId = $(this).attr("data-id");
                            var $apply1 = $(".apply-body:eq(1)");
                            $.ajax({
                                url: "nin-class/getClassList",
                                dataType: "json",
                                type: "post",
                                data: {
                                    careerId: careerId
                                },
                                success: function (data) {
                                    if (data.code == 200) {
                                        $apply1.empty();
                                        var items = $("<div class='items'></div>").text("--班级--");
                                        $apply1.append(items);
                                        var list = data.data;
                                        var len = list.length;
                                        var items = $("<div class='items'></div>");
                                        for (let i = 0; i < len; i++) {
                                            var id = list[i].classId;
                                            var className = list[i].className;
                                            var item = $("<div class='item'></div>").text(className).attr("data-id", id);
                                            items.append(item);
                                            if ((i + 1) % 6 === 0 || i === len - 1) {
                                                $apply1.append(items);
                                                items = $("<div class='items'></div>");
                                            }
                                        }

                                        //点击班级选中
                                        $apply1.find(".item").click(function () {
                                            var classId = $(this).attr("data-id");
                                            var className = $(this).text();
                                            selectedItem(classId, className, "#class");
                                        })
                                        
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
        }


        function selectedItem(id, name, type) {
            var $1 = $("<div></div>").text(name).attr("data-id", id);
            $(type).after($1);
        }


    })
})