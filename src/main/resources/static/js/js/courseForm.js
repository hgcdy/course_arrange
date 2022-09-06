require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {

        $.ajax({
            url: "/nin-arrange/getInfo",
            dataType: "json",
            type: "post",
            data: {
                weekly: null,
                classId: "365938667349718407",
                studentId: null,
                teacherId: null
            },
            success: function (data) {
                if (data.code == 200){
                    var map = data.data;
                    for (const mapKey in map) {
                        var strings = mapKey.split("");
                        var i = strings[0];
                        var j = strings[1];
                        $($($("tr")[j]).find("*")[i]).text(map[mapKey]);
                    }
                }

            }
        })


    })
})