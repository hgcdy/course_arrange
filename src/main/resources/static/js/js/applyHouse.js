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
            $(".apply-body").empty();
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

        $("#button button").click(function () {
            var classIdList = getIdList($("#class"));
            if (classIdList === "[]") {
                util.hint("请选择班级");
                return;
            }
            var houseId = $("#house").next().attr("data-id");
            if (houseId === undefined) {
                util.hint("请选择教室");
                return;
            }
            var teacherId = $("#teacher").next().attr("data-id");
            if (teacherId === undefined) {
                util.hint("请选择教师");
                return;
            }
            var courseId = $("#course").next().attr("data-id");
            if (courseId === undefined) {
                util.hint("请选择课程");
                return;
            }
            var weeklyList = getIdList($("#weekly"));
            if (weeklyList === "[]") {
                util.hint("请选择周次");
                return;
            }
            var weekList = getIdList($("#week"));
            if (weekList === "[]") {
                weekList = "[1,2,3,4,5,6,7]";
            }

            $.ajax({
                url: "nin-arrange/getHouseApplyTime",
                dataType: "json",
                type: "post",
                data: {
                    classIdList: classIdList,
                    houseId: houseId,
                    teacherId: teacherId,
                    courseId: courseId,
                    weeklyList: weeklyList,
                    weekList: weekList
                },
                success: function (data) {
                    if (data.code == 200) {

                        var $apply = $(".apply-body:last");
                        $apply.empty();
                        createItemsText($apply, "--可选时间--");

                        var list = Array();

                        var list1 = data.data;
                        var len = list1.length;
                        var weekly = null;
                        var week = null;
                        for (let i = 0; i < len; i++) {
                            var time = list1[i].split("#");
                            if (time[0] !== weekly || time[1] !== week) {
                                var timeString = util.timeString(time[0], time[1]);
                                createItem($apply, list, ["id", "name"]);
                                createItemsText($apply, timeString);
                                list = Array();
                            }
                            list.push({"id": list1[i], "name": "第" + util.turn(time[2]) + "节课"});
                            if (i === len - 1) {
                                createItem($apply, list, ["id", "name"]);
                            }
                            weekly = time[0];
                            week = time[1];
                        }


                        $(".apply-body:last .item").click(function () {
                            var attr = $(this).attr("data-id");
                            var time = attr.split("#");
                            $.ajax({
                                url: "nin-arrange/submitApply",
                                dataType: "json",
                                type: "post",
                                data: {
                                    classIdList: classIdList,
                                    houseId: houseId,
                                    teacherId: teacherId,
                                    courseId: courseId,
                                    weekly: time[0],
                                    week: time[1],
                                    pitchNum: time[2]
                                },
                                success: function (data) {
                                    if (data.code == 200) {
                                        $(".apply-body:last").empty();
                                    }
                                    util.hint(data.msg);
                                }
                            })
                        })

                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        })



        function getIdList(obj) {
            var nextAll = $(obj).nextAll();
            var str = "[";
            for (const key in nextAll) {
                var id = $(nextAll[key]).attr("data-id");
                if (id !== undefined) {
                    str = str + id + ",";
                } else {
                    break;
                }
            }
            str = str + "]";
            return str;
        }

        function getClass() {
            var $apply = $(".apply-body:first");
            $.ajax({
                url: "nin-career/getCollegeCareerList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        createItemsText($apply, "--学院专业--");
                        var map = data.data;
                        for (const key in map) {
                            createItemsText($apply, key);
                            var list = map[key];
                            createItem($apply, list, ["id", "careerName"])
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
                                        createItemsText($apply1, "--班级--");
                                        var list = data.data;
                                        if (list.length === 0) {
                                            createItemsText($apply1, "该专业暂无设置班级");
                                        }
                                        createItem($apply1, list, ["id", "className"]);



                                        //点击班级选中
                                        $apply1.find(".item").click(function () {
                                            selectedItem($(this).attr("data-id"), $(this).text(), "#class");
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

        function getHouse() {
            var list = Array();
            list.push({"type": -1, "name": "全部"});
            list.push({"type": 0, "name": "教室"});
            list.push({"type": 1, "name": "机房"});
            list.push({"type": 2, "name": "实验室"});

            var $apply = $(".apply-body:first");
            createItemsText($apply, "--教室类型--");
            createItem($apply, list, ["type", "name"]);

            //点击类型展示教室
            $(".apply-body:first").find(".item").click(function () {
                var houseType = $(this).attr("data-id");
                var $apply1 = $(".apply-body:eq(1)");
                $.ajax({
                    url: "nin-house/getHouseByType",
                    dataType: "json",
                    type: "post",
                    data: {
                        houseType: houseType
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            $apply1.empty();
                            createItemsText($apply1, "--教室--");
                            var list = data.data;
                            createItem($apply1, list, ["id", "houseName"]);

                            //点击教室选中
                            $apply1.find(".item").click(function () {
                                selectedItem($(this).attr("data-id"), $(this).text(), "#house");
                            })

                        } else {
                            util.hint(data.msg);
                        }
                    }
                })
            })
        }

        function getTeacher() {
            createItemsText($(".apply-body:first"), "--暂无分类--");
            var $apply1 = $(".apply-body:eq(1)");
            $.ajax({
                url: "nin-teacher/getTeaAll",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        $apply1.empty();
                        createItemsText($apply1, "--教师--");
                        var list = data.data;
                        createItem($apply1, list, ["id", "teacherName"]);
                        //点击教师选中
                        $apply1.find(".item").click(function () {
                            selectedItem($(this).attr("data-id"), $(this).text(), "#teacher");
                        })
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }

        function getCourse() {
            var list = Array();
            list.push({"type": 0, "name": "补课"});
            list.push({"type": -1, "name": "其他用途"});
            var $apply = $(".apply-body:first");
            createItemsText($apply, "--教室用途--");
            createItem($apply, list, ["type", "name"]);

            $(".apply-body:first").find(".item:first").click(function () {
                var $apply1 = $(".apply-body:eq(1)");
                $.ajax({
                    url: "nin-course/getCourseList",
                    dataType: "json",
                    type: "get",
                    success: function (data) {
                        if (data.code == 200) {
                            $apply1.empty();
                            createItemsText($apply1, "--课程--");
                            var list = data.data;
                            createItem($apply1, list, ["id", "courseName"]);
                            //点击课程选中
                            $apply1.find(".item").click(function () {
                                selectedItem($(this).attr("data-id"), $(this).text(), "#course");
                            })
                        } else {
                            util.hint(data.msg);
                        }
                    }
                })
            })
            $apply.find(".item:last").click(function () {
                selectedItem($(this).attr("data-id"), $(this).text(), "#course");
            })


        }

        function getTime() {
            var list = Array();
            for (let i = 1; i <= 20; i++) {
                list.push({"id": i, "name": "第" + util.turn(i) + "周"});
            }
            var $apply = $(".apply-body:first");
            createItemsText($apply, "--周次--");
            createItem($apply, list, ["id", "name"]);
            $apply.find(".item").click(function () {
                selectedItem($(this).attr("data-id"), $(this).text(), "#weekly");
            })

            var list1 = Array();
            for (let i = 1; i <= 6; i++) {
                list1.push({"id": i, "name": "星期" + util.turn(i)});
            }
            list1.push({"id": 7, "name": "星期日"});
            var $apply1 = $(".apply-body:eq(1)");
            createItemsText($apply1, "--星期--");
            createItem($apply1, list1, ["id", "name"]);
            $apply1.find(".item").click(function () {
                selectedItem($(this).attr("data-id"), $(this).text(), "#week");
            })
        }

        //items块标题
        function createItemsText(obj, text) {
            var items = $("<div class='items'></div>").text(text);
            $(obj).append(items);
        }
        //生成item块列表
        function createItem(obj, list, paramNames) {
            var len = list.length;

            var items = $("<div class='items'></div>");
            for (let i = 0; i < len; i++) {
                var id = list[i][paramNames[0]];
                var name = list[i][paramNames[1]];
                var item = $("<div class='item'></div>").text(name).attr("data-id", id);
                items.append(item);
                if ((i + 1) % 5 === 0 || i === len - 1) {
                    $(obj).append(items);
                    items = $("<div class='items'></div>");
                }
            }
        }
        //item块选中
        function selectedItem(id, name, type) {
            var nextAll = $(type).nextAll();

            if (type === "#house" || type === "#teacher" || type === "#course") {
                var attr1 = $(nextAll[0]).attr("data-id");
                if (attr1 !== undefined) {
                    if (attr1 === id) {
                        util.hint("请删除后再重新选择！");
                        return;
                    }
                    $(nextAll[0]).remove();
                }
            } else {
                for (const key in nextAll) {
                    var attr2 = $(nextAll[key]).attr("data-id");
                    if (attr2 !== undefined) {
                        if (id === attr2) {
                            return;
                        }
                    } else {
                        break;
                    }
                }
            }
            var img = $("<img src='../../img/close.png'>").click(function () {
                $(this).parent().remove();
            });
            var $1 = $("<div></div>").text(name + "  ").attr("data-id", id).append(img);
            $(type).after($1);
        }
    })
})