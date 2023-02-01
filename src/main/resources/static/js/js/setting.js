require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });
        var userType = "teacher";
        var courseName = null;
        var openState = null;
        const STR = ["courseName", "state", "openTime", "closeTime"];
        var timer = null;
        query();
        util.navPath("/设置/选课时间设置");

        //状态下拉框
        $("#dropupStateTypeButton").next("ul").find("a").click(function () {
            $("#dropupStateTypeButton").text($(this).text()).attr("open-state", $(this).attr("open-state"));
        })
        //查询按钮
        $("#query").click(function () {
            openState = $("#dropupStateTypeButton").attr("open-state");
            courseName = $("#courseName").val();
            query();
        })
        //重置按钮
        $("#reset").click(function () {
            openState = null;
            $("#dropupCollegeButton").text("状态");
            courseName = null;
            $("#courseName").val(null);
            query();
        })


        //批量修改按钮
        $("#timingBatch").click(function () {
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
                    openState: openState,
                    courseName: courseName
                },
                success: function (data) {
                    if (data.code == 200) {
                        var num = 1;
                        var size = data.data.length;
                        var $tbody = $("tbody");
                        $tbody.empty();
                        $("#checkAll").prop('checked', false);

                        var minDate = null;

                        //生成列表
                        for (let i = 0; i < size; i++) {
                            var obj = data.data[i];

                            if (obj["openState"] === 0) {
                                if ((obj["openTimestamp"] != null && obj["openTimestamp"] < minDate) || minDate == null) {
                                    minDate = obj["openTimestamp"];
                                }
                            } else if (obj["openState"] === 1) {
                                if ((obj["closeTimestamp"] != null && obj["closeTimestamp"] < minDate || minDate == null)) {
                                    minDate = obj["closeTimestamp"];
                                }
                            }
                            if (minDate != null) {
                                if (timer != null) {
                                    clearInterval(timer);
                                }
                                timer = window.setTimeout(function () {
                                    query();
                                },minDate - new Date());
                            }


                            var $tr = $("<tr></tr>");
                            var input = $("<input type='checkbox' name='checkbox' class='setting'>").attr("id", obj["id"]);
                            var $th = $("<th></th>").append(input);
                            var $thNum = $("<th></th>").text(num++).attr("scope", "row");
                            $tr.append($th, $thNum);
                            var len = STR.length
                            for (let j = 0; j < len; j++) {
                                var text = obj[STR[j]];
                                if (j >= len - 2 && text != null) {
                                    text = text.replace("T", " ");
                                }
                                var $td = $("<td></td>").text(text);
                                $tr.append($td);
                            }
                            var bu = "<button type='button' data-openState='2' class='btn btn-success timing'>设置</button>&nbsp;";
                            var $td = $("<td></td>");
                            $td.append(bu);
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
                        $(".timing").click(function () {
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
                           userType: userType,
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
                util.hint("请选择课程");
            }

        }


    })
})