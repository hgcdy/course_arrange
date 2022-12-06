require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var page = 1;
        var size = 10;
        var total = 0;
        var courseName = null;
        var houseType = null;
        var must = null;
        const STR = ["courseName", "cnHouseType", "cnMust", "courseTime", "startTime", "endTime", "weekTime", "maxClassNum"];
        query();
        util.navPath('/资源管理/课程管理');

        //下拉框
        $("#dropupHouseTypeButton").next("ul").find("a").click(function () {
            $("#dropupHouseTypeButton").text($(this).text());
            $("#dropupHouseTypeButton").attr("data-code", $(this).attr("data-code"));
        })
        $("#dropupMustButton").next("ul").find("a").click(function () {
            $("#dropupMustButton").text($(this).text());
            $("#dropupMustButton").attr("data-code", $(this).attr("data-code"));
        })


        //查询按钮
        $("#query").click(function () {
            courseName = $("#courseName").val();
            houseType = $("#dropupHouseTypeButton").attr("data-code");
            if ($.trim(houseType) == "教室类型") {
                houseType = null;
            }
            must = $("#dropupMustButton").attr("data-code");
            if ($.trim(must) == "是否必修") {
                must = null;
            }
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            courseName = null;
            $("#courseName").val(null);
            houseType = null;
            $("#dropupHouseTypeButton").text("教室类型").attr("data-code", "");
            must = null;
            $("#dropupMustButton").text("是否必修").attr("data-code", "");
            query();
        })

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-course/getPageSelectList",
                dataType: "json",
                type: "post",
                data: {
                    courseName: courseName,
                    houseType: houseType,
                    must: must,
                    size: size,
                    page: page
                },
                success: function (data) {
                    if (data.code == 200) {
                        total = data.data.total;
                        if ((data.data.list).length != 0) {
                            $("#page span:eq(2) input").val(page);
                            util.createForm((page - 1) * size + 1, data.data.list, STR, 2);
                            $("#page-text").text("共" + total + "条数据, " + Math.ceil(total / size) + "页");
                            $(".delete").click(function () {
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                del(id);
                            })
                            $(".update").click(function () {
                                alter($(this).parent().parent().children("th"));
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
                url: "nin-course/delCourse",
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
                url: "nin-course/getCourseById",
                dataType: "json",
                type: "post",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var obj = data.data;
                        var $courseName = $("<tr><td><label for='courseName'>课程名称:</label></td><td><input type='text' id='courseName' value=" + obj.courseName + "></td></tr>");

                        var $houseType = $("<tr><td><label for='houseType'>教室类型:</label></td><td><select id='houseType'><option value=0>普通教室</option><option value=1>机房</option><option value=2>实验室</option><option value=3>课外</option><option value=4>网课</option></select></td></tr>");
                        $houseType.find("select").val(obj.houseType);

                        var $must = $("<tr><td><label for='must'>是否必修:</label></td><td><input type='text' id='must' readonly='readonly'><!--<select id='must'><option value=0>选修</option><option value=1>必修</option></select>--></td></tr>");
                        $must.find("input").click(function (){
                            util.hint("不可修改");
                        });
                        if (obj.must == 0) {
                            $must.find("input").val("选修");
                        } else {
                            $must.find("input").val("必修");
                        }

                        var $courseTime = $("<tr><td><label for='courseTime'>课时:</label></td><td><select id='courseTime'><option value=8>8</option><option value=16>16</option><option value=32>32</option><option value=48>48</option><option value=64>64</option></select></td></tr>");
                        $courseTime.find("select").val(obj.courseTime);

                        var $startTime = $("<tr><td><label for='startTime'>开始时间:</label></td><td><input min='1' max='20'  type='number' id='startTime' value=" + obj.startTime + "></td></tr>");

                        var $endTime = $("<tr><td><label for='endTime'>结束时间:</label></td><td><input  min='1' max='20'  type='number' id='endTime' value=" + obj.endTime + "></td></tr>");

                        var $weekTime = $("<tr><td><label for='weekTime'>上课周数:</label></td><td><input  min='1' max='20'  type='number' id='weekTime' value=" + obj.weekTime + "></td></tr>");

                        var $maxClassNum = $("<tr><td><label for='maxClassNum'>上课班级数量:</label></td><td><input  min='1' max='4'  type='number' id='maxClassNum' value=" + obj.maxClassNum + "></td></tr>");

                        $must.click(function (){
                            if ($must.find("select").val() == 0){
                                $courseTime.find("select").val(32);
                                $maxClassNum.find("input").val(1);
                            } else {
                                $courseTime.find("select").val(obj.courseTime);
                                $maxClassNum.find("input").val(obj.maxClassNum);
                            }
                        })
                        util.popup([$courseName, $houseType, $must, $courseTime, $startTime, $endTime, $weekTime, $maxClassNum], ["courseName", "houseType", "must", "courseTime", "startTime", "endTime", "weekTime", "maxClassNum"], $update);
                    }
                }
            })
            function $update(record) {
                if (record.must == 0 && record.num > 32){
                    util.hint("选修课程请勿大于32个课时")
                } else {
                    $.ajax({
                        url: "nin-course/alterCourse",
                        dataType: "json",
                        type: "post",
                        data: {
                            id: id,
                            courseName: record.courseName,
                            houseType: record.houseType,
                            courseTime: record.courseTime,
                            startTime: record.startTime,
                            endTime: record.endTime,
                            weekTime: record.weekTime,
                            maxClassNum: record.maxClassNum
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

        //新增
        $("#insert").click(function () {
            var $courseName = $("<tr><td><label for='courseName'>课程名称:</label></td><td><input type='text' id='courseName'></td></tr>");
            var $houseType = $("<tr><td><label for='houseType'>教室类型:</label></td><td><select id='houseType'><option disabled='disabled' selected='selected'></option><option value=0>普通教室</option><option value=1>机房</option><option value=2>实验室</option><option value=3>课外</option><option value=4>网课</option></select></td></tr>");
            var $must = $("<tr><td><label for='must'>是否必修:</label></td><td><select id='must'><option value=0>选修</option><option value=1>必修</option></select></td></tr>");
            $must.find("select").val(1);
            var $courseTime = $("<tr><td><label for='courseTime'>课时:</label></td><td><select id='courseTime'><option value=8>8</option><option value=16>16</option><option value=32>32</option><option value=48>48</option><option value=64>64</option></select></td></tr>");
            var $startTime = $("<tr><td><label for='startTime'>开始时间:</label></td><td><input min='1' max='20'  type='number' value='1' id='startTime'></td></tr>");

            var $endTime = $("<tr><td><label for='endTime'>结束时间:</label></td><td><input min='1' max='20'  type='number'  value='16' id='endTime'></td></tr>");

            var $weekTime = $("<tr><td><label for='weekTime'>上课周数:</label></td><td><input min='1' max='20'  type='number' value='16' id='weekTime'></td></tr>");

            var $maxClassNum = $("<tr><td><label for='maxClassNum'>上课班级数量:</label></td><td><input min='1' max='4'  type='number' value='2' id='maxClassNum'></td></tr>");

            $must.click(function (){
                if ($must.find("select").val() == 0){
                    $courseTime.find("select").val(32);
                    $maxClassNum.find("input").val(1);
                } else {
                    $courseTime.find("select").val(8);
                }
            })

            util.popup([$courseName, $houseType, $must, $courseTime, $startTime, $endTime, $weekTime, $maxClassNum], ["courseName", "houseType", "must", "courseTime", "startTime", "endTime", "weekTime", "maxClassNum"], $insert);

            function $insert(record) {
                if (record.must == 0 && record.num > 32){
                    util.hint("选修课程请勿大于32个课时")
                } else {
                    $.ajax({
                        url: "nin-course/addCourse",
                        dataType: "json",
                        type: "post",
                        data: {
                            courseName: record.courseName,
                            houseType: record.houseType,
                            must: record.must,
                            courseTime: record.courseTime,
                            startTime: record.startTime,
                            endTime: record.endTime,
                            weekTime: record.weekTime,
                            maxClassNum: record.maxClassNum
                        },
                        success: function (data) {
                            if (data.code == 200) {
                                query();
                                if (record.must == 0){
                                    util.hint("选修课程已生成对应教学班")
                                }
                            } else {
                                util.hint(data.msg);
                            }
                        }
                    })
                }

            }
        })


        $("#mark").mouseout(function (){
            $("#markDiv").css("display", "none");
        })
        $("#mark").mouseover(function (){
            $("#markDiv").css("display", "block");
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
            if (page < 1){
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