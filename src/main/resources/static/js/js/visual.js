require(['../config'], function () {
    require(['jquery', 'util', 'echarts', 'bootstrapBundle'], function ($, util, echarts) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        util.navPath("/课表数据可视化分析");


        $.ajax({
            url: "visualData",
            dataType: "json",
            type: "get",
            success: function (data) {
                if (data.code == 200) {
                    var visualData = data.data;

                    var visualData1 = visualData[0];
                    var visualData2 = visualData[1];
                    var visualData3 = visualData[2];
                    var visualData4 = visualData[3];
                    var visualData5 = visualData[4];
                    var visualData6 = visualData[5];
                    var visualData7 = visualData[6];


                    // 基于准备好的dom，初始化echarts实例
                    var myChart1 = echarts.init(document.getElementById("visual1"));
                    // 指定图表的配置项和数据
                    var option1 = {
                        title: {
                            text: '单周内上课次数占比'
                        },
                        series: [
                            {
                                type: 'pie',
                                data: JSON.parse(visualData1),
                                roseType: 'area'
                            }
                        ]
                    };
                    console.log(option1);
                    // 使用刚指定的配置项和数据显示图表。
                    myChart1.setOption(option1);

                    // 基于准备好的dom，初始化echarts实例
                    var myChart2 = echarts.init(document.getElementById('visual2'));
                    // 指定图表的配置项和数据
                    var option2 = {
                        title: {
                            text: '平均一天内时间段上课次数占比'
                        },
                        series: [
                            {
                                type: 'pie',
                                data: JSON.parse(visualData2)
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart2.setOption(option2);

                    // 基于准备好的dom，初始化echarts实例
                    var myChart3 = echarts.init(document.getElementById('visual3'));
                    // 指定图表的配置项和数据
                    var option3 = {
                        title: {
                            text: '教师单周平均上课次数'
                        },
                        tooltip: {},
                        legend: {
                            data: ['单周平均上课次数']
                        },
                        xAxis: {
                            data: JSON.parse(visualData3)
                        },
                        yAxis: {},
                        series: [
                            {
                                name: '单周平均上课次数',
                                type: 'bar',
                                data: JSON.parse(visualData4)
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart3.setOption(option3);

                    // 基于准备好的dom，初始化echarts实例
                    var myChart4 = echarts.init(document.getElementById('visual4'));
                    // 指定图表的配置项和数据
                    var option4 = {
                        grid: {
                            top: '10%',
                            left: '5%',
                            right: '5%',
                            bottom: '5%',
                            containLabel: true
                        },
                        title: {
                            text: '专业课程数'
                        },
                        tooltip: {},
                        legend: {
                            data: ['课程数量']
                        },
                        xAxis: {

                        },
                        yAxis: {
                            data: JSON.parse(visualData5)
                        },
                        series: [
                            {
                                name: '课程数量',
                                type: 'bar',
                                data: JSON.parse(visualData6),
                                color: "#91c7ae"
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart4.setOption(option4);

                    $("#visual5").text(visualData7);
                } else {
                    $("body").text("暂无数据").css("font-size", "30px");
                    util.hint(data.msg);
                }

            }
        })

    })
})