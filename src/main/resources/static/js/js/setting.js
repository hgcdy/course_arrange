require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var userType = "teacher";
        var state = null;
        var courseName = null;
        const STR = ["courseName", "state", "openTime", "closeTime"];
        query();
        util.navPath("/设置/选课时间设置");

        //状态下拉框
        $("#dropupStateTypeButton").next("ul").find("a").click(function () {
            $("#dropupStateTypeButton").text($(this).text());
        })
        //查询按钮
        $("#query").click(function () {
            state = $("#dropupStateTypeButton").text();
            if ($.trim(state) == "状态") {
                state = null;
            }
            courseName = $("#courseName").val();
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            state = null;
            $("#dropupCollegeButton").text("状态");
            courseName = null;
            $("#courseName").val(null);
            query();
        })


        //批量修改按钮
        $("#disparkBatch, #finishBatch, #timingBatch").click(function () {
            var $input = $("input[name='checkbox']");
            if ($input != null && $input.length > 0) {
                var str = "["
                for (let i = 0; i < $input.length; i++) {
                    if ($($input[i]).is(':checked')) {
                        str = str + $($input[i]).attr("id") + ',';
                    }
                }
                str = str + "]";
                alter(str, $(this).attr("data-openState"));
            }
        })

        //教师学生切换
        $("#stuSetting, #teaSetting").click(function () {
            userType = $(this).attr("data-id");
            $("#stuSetting, #teaSetting").css("border", "");
            $(this).css("border", "1px solid #000000")
            query();
        })

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
                        var num = 1;
                        var size = data.data.length;
                        var $tbody = $("tbody");
                        $tbody.empty();
                        $("#checkAll").prop('checked', false);
                        //生成列表
                        for (let i = 0; i < size; i++) {
                            var $tr = $("<tr></tr>");
                            var input = $("<input type='checkbox' name='checkbox' class='setting'>").attr("id", data.data[i]["id"]);
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

                        //添加全选框
                        $("#checkAll").unbind("click");
                        $("#checkAll").click(function () {
                            if ($("#checkAll").is(':checked')) {
                                $("input[name='checkbox']").prop('checked', true);
                            } else {
                                $("input[name='checkbox']").prop('checked', false);
                            }
                        })
                        $("input[name='checkbox']").click(function () {
                            if (!$(this).is(':checked')) {
                                $("#checkAll").prop('checked', false);
                            }
                        })

                        //添加点击事件
                        $(".dispark, .finish, .timing").click(function () {
                            var id = $(this).parent().parent().children("th").children("input").attr("id");
                            alter("[" + id + "]", $(this).attr("data-openState"));
                        })
                    }
                }
            })
        }

        //修改
        function alter(settingIds, openState) {
            if (settingIds.length > 2) {
                if (openState == 2) {
                    var $openTime = $("<tr><td><label for='openTime'>开始时间:</label></td><td><input type='datetime-local' step=1 id='openTime'></td></tr>");
                    var $closeTime = $("<tr><td><label for='closeTime'>结束时间:</label></td><td><input type='datetime-local' step=1 id='closeTime'></td></tr>");
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
            } else {
                util.hint("请选择课程");
            }

        }


    })
})