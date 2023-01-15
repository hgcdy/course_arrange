define(function () {

    var socket;

    var setCache = function (key, value) {
        //保存到缓存中
        sessionStorage.setItem(key, value);
    }
    var getCache = function (key) {
        //从缓存中获取
        return sessionStorage.getItem(key);
    }
    var delCache = function (key) {
        //删除缓存
        sessionStorage.removeItem(key);
    }

    var setToken = function (token) {
        // 保存token
        setCache('token', token);
    }
    var getToken = function () {
        // 获取token
        return getCache("token");
    }

    /**
     * WebSocket开启
     */
    var openSocket = function () {
        if(typeof(WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        }else{
            console.log("您的浏览器支持WebSocket");
            //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
            //等同于socket = new WebSocket("ws://localhost:8888/xxxx/im/25");
            //var socketUrl="${request.contextPath}/im/"+$("#userId").val();
            var socketUrl="http://" + window.location.host + "/imserver/" + getCache("userId");
            socketUrl=socketUrl.replace("https","ws").replace("http","ws");
            console.log(socketUrl);
            if(socket != null){
                socket.close();
                socket = null;
            }
            socket = new WebSocket(socketUrl);

            //打开事件
            socket.onopen = function() {
                console.log("websocket已打开");
                var obj = {
                    "code": -1,
                    "msg": "xxx",
                    "data": {
                        "userId": getCache("userId")
                    }
                }
                socket.send(JSON.stringify(obj));
            };
            //获得消息事件
            socket.onmessage = function(msg) {
                console.log(msg.data);
                // util.hint("您有一条新消息！");
                //发现消息进入    开始处理前端触发逻辑
            };
            //关闭事件
            socket.onclose = function() {
                console.log("websocket已关闭");
            };
            //发生了错误事件
            socket.onerror = function() {
                console.log("websocket发生了错误");
            }
        }
    }

    /**
     * WebSocket向服务端发送消息
     * @param obj
     */
    var sendMessage = function(obj) {
        if(typeof(WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        }else {
            console.log("您的浏览器支持WebSocket");
            socket.send(obj);
        }
    }

    var navPath = function (obj) {
        var topWindow = $(window.parent.document);
        var $iframe = topWindow.find('#navigation');
        $iframe.text(obj);
    }

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
            var $th = $("<th></th>").text(num++).attr("scope", "row").attr("data-id", data[i]["id"]);
            $tr.append($th)
            for (let j = 0; j < strList.length; j++) {
                var $td = $("<td></td>").text(data[i][strList[j]]);
                $tr.append($td);
            }
            var bu1 = "<button type='button' class='btn btn-info delete'>删除</button>&nbsp;";
            var bu2 = "<button type='button' class='btn btn-info update'>编辑</button>&nbsp;";
            var bu3 = "<button type='button' class='btn btn-info details'>详情</button>&nbsp;";
            var $td = $("<td></td>");
            if (count == 1) {
                $td.append(bu1);
            } else if (count == 2) {
                $td.append(bu1, bu2);
            } else if (count == 3) {
                $td.append(bu1, bu2, bu3);
            } else if (count == 0){
                $td.text("不可操作");
            }
            if (count != -1) {
                $tr.append($td);
            }
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
        var $table = $popup.find("table");
        $table.empty();//todo img问题
        $popup.css("display", "block");
        $("button").attr('disabled', true);
        var len = modules.length;
        $popup.css("height", len * 50 + 100 + "px");

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

    var timeString = function (weekly, week) {
        var x,y;
        x = turn(parseInt(weekly));
        y = turn(parseInt(week));
        if (week == "7") {
            y = "日"
        }
        var str = "第"+ x +"周星期"+ y;
        return str;
    }

    var turn = function(i) {
        var str;
        switch (i) {
            case 1: str = "一";break;
            case 2: str = "二";break;
            case 3: str = "三";break;
            case 4: str = "四";break;
            case 5: str = "五";break;
            case 6: str = "六";break;
            case 7: str = "七";break;
            case 8: str = "八";break;
            case 9: str = "九";break;
            case 10: str = "十";break;
            case 11: str = "十一";break;
            case 12: str = "十二";break;
            case 13: str = "十三";break;
            case 14: str = "十四";break;
            case 15: str = "十五";break;
            case 16: str = "十六";break;
            case 17: str = "十七";break;
            case 18: str = "十八";break;
            case 19: str = "十九";break;
            case 20: str = "二十";break;
        }
        return str;
    }


    return {
        //表格
        createForm: createForm,
        //弹窗
        popup: popup,
        //报错弹幕
        hint: hint,
        //时间转中文字符串
        timeString: timeString,
        //数字转大写数字
        turn: turn,
        //导航栏信息
        navPath: navPath,
        //缓存
        setCache: setCache,
        //从缓存获取
        getCache: getCache,
        //token缓存
        setToken: setToken,
        //获取token
        getToken: getToken,
        openSocket: openSocket,
        sendMessage: sendMessage
};
});