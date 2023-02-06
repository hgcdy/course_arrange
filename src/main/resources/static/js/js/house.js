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
        var houseName = null;
        var houseType = null;
        var firstSeat = null;
        var tailSeat = null;
        const STR = ["houseName", "cnHouseType", "seat"];
        query();
        util.navPath('/资源管理/教室管理');

        //下拉框
        $("#dropupHouseTypeButton").next("ul").find("a").click(function () {
            $("#dropupHouseTypeButton").text($(this).text());
            $("#dropupHouseTypeButton").attr("data-code", $(this).attr("data-code"));
        })

        //查询按钮
        $("#query").click(function () {
            houseName = $("#houseName").val();
            houseType = $("#dropupHouseTypeButton").attr("data-code");
            if ($.trim(houseType) == "教室类型") {
                houseType = null;
            }
            firstSeat = $("#seat1").val();
            tailSeat = $("#seat2").val();
            if (!isNaN(firstSeat) && !isNaN(tailSeat)){
                query();
            }else{
                util.hint("请输入正确的座位数！");
            }
        })
        //重置按钮
        $("#reset").click(function () {
            houseName = null;
            $("#houseName").val(null);
            houseType = null;
            $("#dropupHouseTypeButton").text("教室类型").attr("data-code", "");
            firstSeat = null;
            $("#seat1").val(null);
            tailSeat = null;
            $("#seat2").val(null);
            query();
        })


        // 获取数据
        function query() {
            $.ajax({
                url: "nin-house/getPageSelectList",
                dataType: "json",
                type: "get",
                data: {
                    houseName: houseName,
                    houseType: houseType,
                    firstSeat: firstSeat,
                    tailSeat: tailSeat,
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
                url: "nin-house/delHouse",
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
                url: "nin-house/getHouseById",
                dataType: "json",
                type: "get",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code == 200) {
                        var obj = data.data;
                        var $houseName = $("<tr><td><label for='houseName'>教室名称:</label></td><td><input type='text' id='houseName' value=" + obj.houseName + "></td></tr>");

                        var $houseType = $("<tr><td><label for='houseType'>教室类型:</label></td><td><select id='houseType'><option value=0>普通教室</option><option value=1>机房</option><option value=2>实验室</option></select></td></tr>");
                        $houseType.find("select").val(obj.houseType);

                        var $seat = $("<tr><td><label for='seat'>教室座位:</label></td><td><input type='number' id='seat' value=" + obj.seat + "></td></tr>");

                        util.popup([$houseName, $houseType, $seat], ["houseName", "houseType", "seat"], $update);
                    }
                }
            })

            function $update(record) {
                $.ajax({
                    url: "nin-house/alterHouse",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id,
                        houseName: record.houseName,
                        houseType: record.houseType,
                        seat: record.seat
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
            var $houseName = $("<tr><td><label for='houseName'>教室名称:</label></td><td><input type='text' id='houseName'></td></tr>");
            var $houseType = $("<tr><td><label for='houseType'>教室类型:</label></td><td><select id='houseType'><option disabled='disabled' selected='selected'></option><option value=0>普通教室</option><option value=1>机房</option><option value=2>实验室</option></select></td></tr>");
            var $seat = $("<tr><td><label for='seat'>教室座位:</label></td><td><input type='number' id='seat'></td></tr>");
            util.popup([$houseName, $houseType, $seat], ["houseName", "houseType", "seat"], $insert);

            function $insert(record) {
                $.ajax({
                    url: "nin-house/addHouse",
                    dataType: "json",
                    type: "post",
                    data: {
                        houseName: record.houseName,
                        houseType: record.houseType,
                        seat: record.seat
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