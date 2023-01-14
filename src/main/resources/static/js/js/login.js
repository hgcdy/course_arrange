require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

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
                                util.setToken(token);
                                util.set("userId", userId);
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