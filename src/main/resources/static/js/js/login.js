require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        $("button").click(function () {
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
                                window.location.href = "index";
                            } else {
                                util.hint("登录失败，请重试！");
                            }
                        }
                    })
                } else {
                    util.hint("请输入密码！");
                }
            } else {
                util.hint("请输入账号！");
            }
        })


    })
})