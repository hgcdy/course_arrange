require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        var college = null;
        var careerId = null;

        query();

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200) {
                        var $chunkElement = $(".chunk-card")[0];
                        item($($chunkElement), data.data);
                    } else {
                        util.hint(data.msg)
                    }
                }
            })
        }


        function item(obj, list) {
            for (const item in list) {



            }
        }
    })
})