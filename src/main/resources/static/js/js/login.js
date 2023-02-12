require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        if(top.location != location){
            util.hint("您的账号在异地登录，请重新登录");
            window.setTimeout(function () {
                top.location.href= location.href;
            }, 500);
        }

        //回车触发登录
        $("html").keydown(function (event) {
            if (event.keyCode == 13) {
                login();
            }
        });

        //点击触发登录
        $("button").click(function () {
            login();
        });

        //登录
        function login() {
            var code = $("#floatingCode").val();
            var password = $("#floatingPassword").val();
            var val = $('input[name="identity"]:checked').val();
            if (code != null && code != "") {
                if (password != null && password != "") {
                    $.ajax({
                        url: "login/" + val,
                        dataType: "json",
                        type: "post",
                        data: {
                            code: code,
                            password: password
                        },
                        success: function (data) {
                            if (data.code == 200) {
                                var token = data.data["token"];
                                var userId = data.data["userId"];
                                var role = data.data["role"];
                                util.setToken(token);
                                util.setCache("userId", userId);
                                util.setCache("role", role);
                                if (role !== "admin") {
                                    util.setCache("detailId", userId);
                                    util.setCache("type", role);
                                }
                                window.location.href = "index?type="+ val +"&token=" + token;
                            } else {
                                util.hint(data.msg);
                            }
                        }
                    })
                } else {
                    util.hint("请输入密码！");
                }
            } else {
                util.hint("请输入账号！");
            }
        };
    })
})