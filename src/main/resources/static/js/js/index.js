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
            var attr = $(this).attr("data-type");
            if (attr !== undefined) {
                util.setCache("type", attr);
                util.setCache("detailId", util.getCache("userId"));
            }
            $("#popup").css("display", "none").find("table").empty();
        })

        util.openSocket();


        // var socket;
        // function openSocket() {
        //     if(typeof(WebSocket) == "undefined") {
        //         console.log("您的浏览器不支持WebSocket");
        //     }else{
        //         console.log("您的浏览器支持WebSocket");
        //         //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
        //         //等同于socket = new WebSocket("ws://localhost:8888/xxxx/im/25");
        //         //var socketUrl="${request.contextPath}/im/"+$("#userId").val();
        //         var socketUrl="http://localhost:8080/imserver/" + util.getCache("userId");
        //         socketUrl=socketUrl.replace("https","ws").replace("http","ws");
        //         console.log(socketUrl);
        //         if(socket!=null){
        //             socket.close();
        //             socket=null;
        //         }
        //         socket = new WebSocket(socketUrl);
        //         //打开事件
        //         socket.onopen = function() {
        //             console.log("websocket已打开");
        //             var obj = {
        //                 "code": -1,
        //                 "msg": "xxx",
        //                 "data": {
        //                     "userId": util.getCache("userId")
        //                 }
        //             }
        //             socket.send(JSON.stringify(obj));
        //         };
        //         //获得消息事件
        //         socket.onmessage = function(msg) {
        //             console.log(msg.data);
        //             // util.hint("您有一条新消息！");
        //             //发现消息进入    开始处理前端触发逻辑
        //         };
        //         //关闭事件
        //         socket.onclose = function() {
        //             console.log("websocket已关闭");
        //         };
        //         //发生了错误事件
        //         socket.onerror = function() {
        //             console.log("websocket发生了错误");
        //         }
        //     }
        // }
        // function sendMessage() {
        //     if(typeof(WebSocket) == "undefined") {
        //         console.log("您的浏览器不支持WebSocket");
        //     }else {
        //         console.log("您的浏览器支持WebSocket");
        //         socket.send("这是来自客户端的消息" + location.href + new Date());
        //     }
        // }

    })
})