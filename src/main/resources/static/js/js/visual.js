require(['../config'], function () {
    require(['jquery', 'util', 'echarts', 'bootstrapBundle'], function ($, util, echarts) {
        $.ajaxSetup({ //发送请求前触发
            beforeSend: function (xhr) { //可以设置自定义标头
                xhr.setRequestHeader('token', util.getToken());
            }
        });

        util.navPath("/课表数据可视化分析");
        var count = 0;
        query();

        $("button").click(function () {
            $.ajax({
                url: "delVisualData",
                dataType: "json",
                type: "get",
                success: function (data) {
                    util.hint(data.msg);
                    query();
                }
            })
        })


        var myChart1 = echarts.init(document.getElementById("visual1"));
        var myChart2 = echarts.init(document.getElementById("visual2"));
        var myChart3 = echarts.init(document.getElementById("visual3"));
        var myChart4 = echarts.init(document.getElementById("visual4"));
        var myChart5 = echarts.init(document.getElementById("visual5"));
        var myChart6 = echarts.init(document.getElementById("visual6"));
        var myChart7 = echarts.init(document.getElementById("visual7"));
        var myChartList = [
            myChart1,
            myChart2,
            myChart3,
            myChart4,
            myChart5,
            myChart6,
            myChart7
        ]
        var textSeq = [3,2,1,0,6,5,4];


        //点击切换
        $(".click").click(function () {
            for (let i = 0; i < 7; i++) {
                myChartList[i].clear();
            }
            count = (count + parseInt($(this).attr("data-count"))) % 7;
            query();
        })

        function query() {
            $.ajax({
                url: "visualData",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        var visualData = data.data;
                        fun1(visualData[0]);
                        fun2(visualData[1]);
                        fun3(visualData[2]);
                        fun4(visualData[3]);
                        fun5(visualData[4]);
                        fun6(visualData[5]);
                        fun7(visualData[6]);
                        // $("#text").text(visualData[7][textSeq[count]]);
                    } else {
                        util.hint(data.msg);
                    }
                }
            })
        }

        function fun1(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[count % 7];
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '星期上课次数占比'
                },
                tooltip: {
                    trigger: 'item'
                },
                series: [
                    {
                        type: 'pie',
                        data: parse,
                        roseType: 'area'
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun2(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 1) % 7];
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '平均一天内时间段占比'
                },
                tooltip: {
                    trigger: 'item'
                },
                series: [
                    {
                        type: 'pie',
                        data: parse
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun3(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 2) % 7];
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '教师-平均单周上课次数柱形图'
                },
                tooltip: {},
                legend: {
                    data: ['平均单周上课次数']
                },
                xAxis: {
                    data: parse[0]
                },
                yAxis: {},
                series: [
                    {
                        name: '平均单周上课次数',
                        type: 'bar',
                        data: parse[1]
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun4(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 3) % 7];
            // 指定图表的配置项和数据
            var option = {
                grid: {
                    top: '10%',
                    left: '5%',
                    right: '5%',
                    bottom: '5%',
                    containLabel: true
                },
                title: {
                    text: '专业-选择课程数柱形图'
                },
                tooltip: {},
                legend: {
                    data: ['课程数量']
                },
                xAxis: {

                },
                yAxis: {
                    data: parse[0]
                },
                series: [
                    {
                        name: '课程数量',
                        type: 'bar',
                        data: parse[1],
                        color: "#91c7ae"
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun5(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 4) % 7];
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '课程-上课班级数量漏斗图'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c}%'
                },
                toolbox: {
                    feature: {
                        dataView: { readOnly: false },
                        restore: {},
                        saveAsImage: {}
                    }
                },
                legend: {
                    data: parse[0],
                    y: "bottom"
                },
                series: [
                    {
                        name: 'Funnel',
                        type: 'funnel',
                        left: '10%',
                        top: 60,
                        bottom: 60,
                        width: '80%',
                        min: 0,
                        // max: 100,
                        minSize: '0%',
                        maxSize: '100%',
                        sort: 'descending',
                        gap: 2,
                        label: {
                            show: true,
                            position: 'inside'
                        },
                        labelLine: {
                            length: 10,
                            lineStyle: {
                                width: 1,
                                type: 'solid'
                            }
                        },
                        itemStyle: {
                            borderColor: '#fff',
                            borderWidth: 1
                        },
                        emphasis: {
                            label: {
                                fontSize: 20
                            }
                        },
                        data: parse[1]
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun6(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 5) % 7];
            // 指定图表的配置项和数据
            var option = {
                title: [
                    {
                        text: '教室-平均单周使用次数极坐标柱形图'
                    }
                ],
                polar: {
                    radius: [30, '80%']
                },
                angleAxis: {
                    // max: 20,
                    startAngle: 75
                },
                radiusAxis: {
                    type: 'category',
                    data: parse[0]
                },
                tooltip: {},
                series: {
                    type: 'bar',
                    data: parse[1],
                    coordinateSystem: 'polar',
                    label: {
                        show: true,
                        position: 'middle',
                        formatter: '{b}: {c}'
                    }
                }
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
        function fun7(data) {
            var parse = JSON.parse(data);

            // 基于准备好的dom，初始化echarts实例
            var myChart = myChartList[(count + 6) % 7];
            // 指定图表的配置项和数据
            // prettier-ignore
            const hours = [
                '星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'
            ];
            // prettier-ignore
            const days = [
                '第一节', '第二节', '第三节','第四节', '第五节'
            ];
            // prettier-ignore
            const dataList = parse
                .map(function (item) {
                    return [item[1], item[0], item[2] || '-'];
                });
            var option = {
                title: [
                    {
                        text: '时间-平均单周上课次数热力图'
                    }
                ],
                tooltip: {
                    position: 'top'
                },
                grid: {
                    height: '50%',
                    top: '10%'
                },
                xAxis: {
                    type: 'category',
                    data: hours,
                    splitArea: {
                        show: true
                    }
                },
                yAxis: {
                    type: 'category',
                    data: days,
                    splitArea: {
                        show: true
                    }
                },
                visualMap: {
                    min: 0,
                    max: 10,
                    calculable: true,
                    orient: 'horizontal',
                    left: 'center',
                    bottom: '15%'
                },
                series: [
                    {
                        name: '上课次数',
                        type: 'heatmap',
                        data: dataList,
                        label: {
                            show: true
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }

    })
})