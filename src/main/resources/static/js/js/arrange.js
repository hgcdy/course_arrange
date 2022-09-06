require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $("button").click(function (){
            $.ajax({
                url: "nin-arrange/arrange",
                dataType:"json",
                type: "get",
                success:function (){
                    util.hint("生成成功！");
                }
            })
        })



    })
})