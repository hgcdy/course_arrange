require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        var size = 5;
        var page = 1;
        var totalPage = null;

        util.navPath("/设置/用户信息");
        getUserInfo();
        getMsgList();
        var role = util.getCache("role");
        if (role === "admin") {
            $("button").attr("disabled", "disabled");
            $("#but").click(function () {
                util.hint("无法操作");
            });
        } else {
            $("button").click(function () {
                alterPassword();
            })
        }


        var $a = $("#operate a");
        $($a[0]).click(function () {
            //一键已读
            readMag(null);
        })
        $($a[1]).click(function () {
            //一键删除
            delMsg(null);
        })
        $($a[1]).click(function () {
            //上一页
            if (page > 1)
                page = page - 1;
            getMsgList();
        })
        $($a[1]).click(function () {
            //刷新
            getMsgList();
        })
        $($a[1]).click(function () {
            //下一页
            if (page < totalPage)
                page = page + 1;
            getMsgList();
        })

        //获取用户信息
        function getUserInfo() {
            $.ajax({
                url: "getUserInfo",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code === 200) {
                        var $td = $("td");
                        $($td[1]).text(data.data.userName);
                        $($td[3]).text(data.data.userCode);
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }
        //修改密码
        function alterPassword() {
            var $input = $("input");
            var old = $($input[0]).text();
            var new1 = $($input[1]).text();
            var new2 = $($input[2]).text();

            if (old === "") {
                util.hint("旧密码为空");
                return;
            }
            if (new1 === "" || new2 === "" || new1 === new2) {
                util.hint("新密码不一致")
                return;
            }

            $.ajax({
                url: "alterPassword",
                dataType: "json",
                type: "post",
                data: {
                    oldPassword: old,
                    newPassword: new1
                },
                success: function (data) {
                    util.hint(data.msg);
                }
            })
        }

        //获取消息列表
        function getMsgList() {
            $.ajax({
                url: "nin-message/getMsgList",
                dataType: "json",
                type: "get",
                data: {
                    page: page,
                    size: size
                },
                success: function (data) {
                    if (data.code === 200) {
                        $("#msg-body").empty();
                        var total = data.data.total;
                        totalPage = Math.ceil(total / 5.0);
                        var list = data.data.records;
                        var len = list.length;
                        for (let i = 0; i < len; i++) {
                            var record = list[i];
                            var $item = $("<div class='mag-item'></div>").css("top", i * 20 + "%");
                            var $text = $("<div class='msg-text'></div>").text(record.msg);
                            var $operate = $("<div class='msg-operate'></div>").attr("data-id", record.id);

                            var a1 = "<a href='javaScript:void(0)' class='read'>已读</a>";
                            var a2 = "<a href='javaScript:void(0)' class='del'>删除</a>";
                            var a3 = "<a href='javaScript:void(0)' class='consent'>同意</a>";
                            var a4 = "<a href='javaScript:void(0)' class='unconsent'>退回</a>";

                            var isRead = record.isRead;
                            var isConsent = record.isConsent;
                            if (isConsent === 0) {
                                $operate.append("<br>", a3, "<br>", "<br>", a4);
                            } else if (isRead === 0) {
                                $operate.append("<br>", a1);
                            } else {
                                $operate.append("<br>", a2);
                            }
                            $item.append($text, $operate);
                            $("#msg-body").append($item);
                        }

                        //已读
                        $(".read").click(function () {
                            var id = $(this).parent().attr("data-id");
                            readMag(id);
                        })
                        //删除
                        $(".read").click(function () {
                            var id = $(this).parent().attr("data-id");
                            delMsg(id);
                        })
                        //同意
                        $(".consent").click(function () {
                            var id = $(this).parent().attr("data-id");
                            //todo
                        })
                        //退回
                        $(".unconsent").click(function () {
                            var id = $(this).parent().attr("data-id");
                        })

                    } else {
                        util.hint(data.msg);
                    }
                }
            })


        }


        //已读
        function readMag(id) {
            $.ajax({
                url: "nin-message/readMag",
                dataType: "json",
                type: "post",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code === 200) {
                        if (id === null) {
                            page = 1;
                        }
                        getMsgList();
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }
        //删除
        function delMsg(id) {
            $.ajax({
                url: "nin-message/delMsg",
                dataType: "json",
                type: "post",
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.code === 200) {
                        if (id === null) {
                            page = 1;
                        }
                        getMsgList();
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }


    })
})