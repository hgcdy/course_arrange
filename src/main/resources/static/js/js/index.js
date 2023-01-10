require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        //退出登录
        $("header button").click(function () {
            window.location.href = "logout?token=" + util.getToken();
        })

        /**
         * 页面跳转
         */
        $("#menu a").click(function () {
            var path = $(this).attr("data-html");
            path = path + "?token=" + util.getToken();
            $("iframe").attr("src", path);
            $("#popup").css("display", "none");
            $("#popup").find("table").empty();
        })
    })
})