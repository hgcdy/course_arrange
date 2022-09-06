define(function () {

    /**
     * 查询
     * @param num 编号 该页第一个的序号
     * @param data 类列表
     * @param strList 类属性列表["属性1", "属性2", "属性3"]
     * @param count 类型编号（控制按钮数量） 1.删除, 2.删除、编辑, 3.删除、编辑、详情
     */
    var createForm = function (num, data, strList, count) {
        var size = data.length;
        var $tbody = $("tbody");
        if (count != 0){
            $tbody.empty();
        }
        for (let i = 0; i < size; i++) {
            var $tr = $("<tr></tr>");
            var $th = $("<th></th>").text(num++).attr("scope", "row").attr("data-id", data[i]["id"])
            $tr.append($th)
            for (let j = 0; j < strList.length; j++) {
                var $td = $("<td></td>").text(data[i][strList[j]]);
                $tr.append($td);
            }
            var bu1 = "<button type='button' class='btn btn-info delete'>删除</button>&nbsp;";
            var bu2 = "<button type='button' class='btn btn-info update'>编辑</button>&nbsp;";
            var bu3 = "<button type='button' class='btn btn-info details'>详情</button>&nbsp;";
            var $td = $("<td></td>").css("width", "200px");
            if (count == 1) {
                $td.append(bu1);
            } else if (count == 2) {
                $td.append(bu1, bu2);
            } else if (count == 3) {
                $td.append(bu1, bu2, bu3);
            } else if (count == 0){
                $td.text("不可操作");
            }
            $tr.append($td);
            $tbody.append($tr);
        }
    }


    /**
     *
     * @param modules 模块列表
     * @param ids 属性名称列表
     * @param $operate 完成后的操作方法
     */
    var popup = function (modules, ids, $operate) {

        var $popup = $("#popup", parent.document);
        $popup.css("display", "block");
        $("button").attr('disabled', true);
        var len = modules.length;
        $popup.css("height", len * 50 + 100 + "px");
        var $table = $popup.find("table");
        for (let i = 0; i < len; i++) {
            $table.append(modules[i]);
        }

        $popup.children("#but").children("#confirm").click(function () {
            var $input = $table.find("input");
            for (let i = 0; i < $input.length; i++) {
                if ($($input[i]).val() == null || $($input[i]).val() == "") {
                    hint("请输入完整，不要留空")
                    return;
                }
            }
            var $select = $table.find("select");
            for (let i = 0; i < $select.length; i++) {
                if ($($select[i]).val() == null || $($select[i]).val() == "") {
                    hint("请输入完整，不要留空")
                    return;
                }
            }

            var record = {};
            for (let i = 0; i < ids.length; i++) {
                record[ids[i]] = $($table).find("#" + ids[i]).val();
            }
            $table.empty();
            $popup.css("display", "none");
            $("button").attr('disabled', false);
            $(this).unbind();
            $operate(record);
        })

        $popup.children("#but").children("#cancel").click(function () {
            $("button").attr('disabled', false);
            $popup.css("display", "none");
            $table.empty();
        })
    }

    var hint = function (text) {
        var $hint = $("#hint", parent.document);
        if ($hint == null) {
            $hint = $("#hint");
        }
        $hint.text(text).css("display", "block");
        window.setTimeout(function () {
            $hint.text("").css("display", "none");
        }, 1000)
    }




    return {
        createForm: createForm,
        popup: popup,
        hint: hint
    };
});