require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var userType = "teacher";
        var state = null;
        var courseName = null;
        var settingIds = null;
        const STR = ["courseName", "state", "openTime", "closeTime"];
        query();

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-setting/getSelectList",
                dataType: "json",
                type: "get",
                data: {
                    userType: userType,
                    state: state,
                    courseName: courseName
                },
                success: function (data) {
                    if (data.code == 200) {
                        if ((data.data).length != 0) {
                            var num = 1;
                            var size = data.data.length;
                            var $tbody = $("tbody");
                            $tbody.empty();
                            for (let i = 0; i < size; i++) {
                                var $tr = $("<tr></tr>");
                                var input = $("<input type='checkbox' class='setting'>").attr("id", data.data[i]["id"]);
                                var $th = $("<th></th>").append(input);
                                var $thNum = $("<th></th>").text(num++).attr("scope", "row");
                                $tr.append($th, $thNum);
                                for (let j = 0; j < STR.length; j++) {
                                    var $td = $("<td></td>").text(data.data[i][STR[j]]);
                                    $tr.append($td);
                                }
                                var bu1 = "<button type='button' data-openState='0' class='btn btn-info dispark'>开放</button>&nbsp;";
                                var bu2 = "<button type='button' data-openState='1' class='btn btn-info finish'>不开放</button>&nbsp;";
                                var bu3 = "<button type='button' data-openState='2' class='btn btn-info timing'>定时开放</button>&nbsp;";
                                var $td = $("<td></td>");
                                $td.append(bu1, bu2, bu3);
                                $tr.append($td);
                                $tbody.append($tr);
                            }
                            $(".dispark, .finish, .timing").click(function () {
                                var id = $(this).parent().parent().children("th").children("input").attr("id");
                                settingIds = "[" + id + "]";
                                alter(settingIds, $(this).attr("data-openState"));
                            })

                        }
                    }
                }
            })
        }


        function alter(settingIds, openState) {
            if (openState == 2) {
                var $openTime = $("<tr><td><label for='openTime'>开始时间:</label></td><td><input type='datetime-local' id='openTime'></td></tr>");
                var $closeTime = $("<tr><td><label for='closeTime'>结束时间:</label></td><td><input type='datetime-local' id='closeTime'></td></tr>");
                util.popup([$openTime, $closeTime], ["openTime", "closeTime"], $update);
                function $update(record) {
                    $.ajax({
                        url: "nin-setting/alterBatch",
                        dataType: "json",
                        type: "post",
                        data: {
                            settingIds: settingIds,
                            openState: openState,
                            openTime: record.openTime,
                            closeTime: record.closeTime
                        },
                        success: function (data) {
                            util.hint(data.msg);
                            if (data.code == 200) {
                                query();
                            }
                        }
                    })
                }
            } else {
                $.ajax({
                    url: "nin-setting/alterBatch",
                    dataType: "json",
                    type: "post",
                    data: {
                        settingIds: settingIds,
                        openState: openState
                    },
                    success: function (data) {
                        util.hint(data.msg);
                        if (data.code == 200) {
                            query();
                        }
                    }
                })
            }




        }

    })
})