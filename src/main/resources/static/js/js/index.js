require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        $("h3").parent("a").click(function () {
            window.location.href = window.location.href;
        })

        //退出登录
        $("header button").click(function () {
            window.location.href = "logout?token=" + util.getToken();
        })


        //刷新token
        window.setInterval(function () {
            $.ajax({
                url: "refreshToken",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code === 200) {
                        util.setCache("token", data.data);
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }, 1000 * 60 * 60)


        /**
         * 页面跳转
         */
        $("#menu a").click(function () {
            var path = $(this).attr("data-html");
            path = path + "?token=" + util.getToken();
            $("iframe").attr("src", path);
            $("#popup").css("display", "none").find("table").empty();
        })

    })
})