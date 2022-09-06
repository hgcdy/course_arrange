require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        //退出登录
        $("header button").click(function () {
            window.location.href = "logout";
        })

        /**
         * 页面跳转
         */
        $("#menu a").click(function () {
            var path = $(this).attr("data-html");
            $("iframe").attr("src", path);
            $("#popup").css("display", "none");
            $("#popup").find("table").empty();
        })
    })
})