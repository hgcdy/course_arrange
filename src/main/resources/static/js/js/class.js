require(['../config'], function () {
    require(['jquery', 'util', 'bootstrapBundle'], function ($, util) {
        var page = 1;
        var size = 10;
        var total = 0;
        var college = null;
        var careerId = null;
        var careerName = null;
        var className = null;
        const STR = ["college", "careerName", "className", "peopleNum", "courseNum"];
        query();

        // todo 选择学院后选择专业

        //学院选择下拉框
        $("#dropupCollegeButton").click(function (){
            //todo 重置专业选择

            $.ajax({
                url: "nin-career/getCollegeList",
                dataType: "json",
                type: "post",
                success: function (data) {
                    if (data.code == 200){
                        var list = data.data;
                        $("#dropupCollegeButton").next("ul").empty();
                        for (let i = 0; i < list.length; i++) {
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i]).click(function (){
                                $("#dropupCollegeButton").text($(this).text());
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupCollegeButton").next("ul").append($li);
                        }
                    }
                }
            })
        })

        //专业选择下拉框
        $("#dropupCareerButton").click(function (){
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "学院"){
                college = null;
            }

            $.ajax({
                url: "nin-career/getCareerList",
                dataType: "json",
                type: "post",
                data: {
                    college: college
                },
                success: function (data) {
                    if (data.code == 200){
                        var list = data.data;
                        $("#dropupCareerButton").next("ul").empty();
                        //todo 有问题，还没看
                        for (let i = 0; i < list.length; i++) {
                            var $a = $("<a class='dropdown-item' href='javaScript:void(0)'></a>").text(list[i]).click(function (){
                                $("#dropupCareerButton").text($(this).text());
                            });
                            var $li = $("<li></li>").append($a);
                            $("#dropupCareerButton").next("ul").append($li);
                        }
                    }
                }
            })
        })


        //查询按钮
        $("#query").click(function (){
            college = $("#dropupCollegeButton").text();
            if ($.trim(college) == "专业"){
                college = null;
            }
            className = $("#className").val();
            query();
        })
        //重置按钮
        $("#reset").click(function (){
            college = null;
            $("#dropupCareerButton").text("专业");
            className = null;
            $("#className").val(null);
            query();
        })

        // 获取数据
        function query() {
            $.ajax({
                url: "nin-class/getPageSelectList",
                dataType: "json",
                type: "post",
                data: {
                    college: college,
                    careerId: careerId,
                    className: className,
                    size: size,
                    page: page
                },
                success: function (data) {
                    if (data.code == 200) {
                        total = data.data.total;
                        if ((data.data.list).length != 0){
                            $("#page span:eq(2) input").val(page);
                            util.createForm((page - 1) * size + 1, data.data.list, STR, 3);
                            $("#page-text").text("共" + total + "条数据, " + Math.ceil(total / size) +"页");
                            $(".delete").click(function (){
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                del(id);
                            })
                            $(".update").click(function (){
                                alter($(this).parent().parent().children("th"));
                            })
                            //详情
                            $(".details").click(function (){
                                var id = $(this).parent().parent().children("th").attr("data-id");
                                window.location.href = "nin-class-course?classId=" + id;
                            })
                        }else{
                            if (page > 1){
                                page = page - 1;
                                query();
                            }else {
                                $("tbody").empty();
                            }
                        }
                    }
                }
            })
        }


        //批量新增课程
        $("#addBatchCourse").click(function (){
            $.ajax({
                url: "nin-course/getSelectCourseList",
                dataType: "json",
                type: "post",
                data: {
                    classId: 2
                },
                //获取课程列表
                success: function (data) {
                    if (data.code == 200) {
                        var courseList = data.data;
                        var $career = $("<tr><td><label for='career'>专业:</label></td></tr>");
                        var $td = $("<td></td>")
                        var $select = $("<select id='career'></select>");
                        var $op = $("<option disabled='disabled' selected='selected'></option>");
                        $($select).append($op);
                        box($select);
                        $td.append($select);
                        $career.append($td);

                        var $courseId = $("<tr><td><label for='courseId'>课程:</label></td></tr>");
                        var $td1 = $("<td></td>")
                        var $select1 = $("<select id='courseId'></select>");
                        var $op1 = $("<option disabled='disabled' selected='selected'></option>");
                        $($select1).append($op1);
                        for (let i = 0; i < courseList.length; i++) {
                            var $option = $("<option></option>").text(courseList[i].courseName).val(courseList[i].id);
                            $select1.append($option);
                        }
                        $td1.append($select1);
                        $courseId.append($td1);
                        // var $tr = $("<tr><td colspan='2'>为选择的班级添加该课程(不包含选修教学班)</td>></tr>");
                        // $("#module", parent.document).find("table").append($tr);
                        util.popup([$career, $courseId], ["career", "courseId"], $insert);
                    }
                }
            })
            function $insert(record){
                // var text = $("#dropupCareerButton").text();
                // if ($.trim(text) == "专业"){
                //     text = null;
                // }


                $.ajax({
                    url: "nin-class-course/addBatchClassCourse",
                    dataType: "json",
                    type: "post",
                    data: {
                        career: record.career,
                        courseId: record.courseId
                    },
                    success: function (data){
                        if (data.code == 200){
                            query();
                        }
                    }
                })

            }

        })

        function box($select) {
            $.ajax({
                url: "nin-class/careerList",
                dataType: "json",
                type: "get",
                success: function (data) {
                    if (data.code == 200) {
                        var list = data.data;
                        for (let i = 0; i < list.length; i++) {
                            if (list[i] == "#"){
                                continue;
                            }
                            var $op = $("<option></option>").text(list[i]).val(list[i]);
                            $($select).append($op);
                        }
                    }
                }
            })
        }

        //新增
        $("#insert").click(function (){
            var $career = $("<tr><td><label for='career'>专业:</label></td><td><input type='text' id='career'></td></tr>");
            var $className = $("<tr><td><label for='className'>班级名称:</label></td><td><input type='text' id='className'></td></tr>");
            // var $label = $("<tr><td><label for='sex'>性别:</label></td><td><select id='sex'><option value='男'>男</option><option value='女'>女</option></select></td></tr>");
            util.popup([$career, $className], ["career", "className"], $insert);

            function $insert(record){
                $.ajax({
                    url: "nin-class/addClass",
                    dataType: "json",
                    type: "post",
                    data: {
                        career: record.career,
                        className: record.className
                    },
                    success: function (data){
                        if (data.code == 200){
                            query();
                        }else{
                            alert(data.msg);
                        }
                    }
                })
            }
        })

        //编辑
        function alter($id){
            var id = $($id).attr("data-id");
            var nextAll = $($id).nextAll();
            var $career = $("<tr><td><label for='career'>专业:</label></td><td><input type='text' id='career' value="+ $(nextAll[0]).text() +"></td></tr>");
            var $className = $("<tr><td><label for='className'>班级名称:</label></td><td><input type='text' id='className' value="+$(nextAll[1]).text()+"></td></tr>");
            // var $label = $("<tr><td><label for='sex'>性别:</label></td><td><select id='sex'><option value='男'>男</option><option value='女'>女</option></select></td></tr>");
            util.popup([$career, $className], ["career", "className"], $update);

            function $update(record){
                $.ajax({
                    url: "nin-class/alterClass",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id,
                        career: record.career,
                        className: record.className
                    },
                    success: function (data){
                        if (data.code == 200){
                            query();
                        }else{
                            alert(data.msg);
                        }
                    }
                })
            }
        }

        //删除
        function del(id) {
            var $tr = $("<tr><td colspan='2'>删除班级会将与该班级有关的所有信息一起删除</td>></tr>");
            $("#module", parent.document).find("table").append($tr);
            util.popup([], [], function (){
                $.ajax({
                    url: "nin-class/delClass",
                    dataType: "json",
                    type: "post",
                    data: {
                        id: id
                    },
                    success: function (data) {
                        if (data.code == 200){
                            query();
                        }
                    }
                })
            });
        }


        //切换每页记录条数
        $("#page a:eq(0)").click(function () {
            size = 10;
            page = 1;
            $("#page button:first").text("X10");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        $("#page a:eq(1)").click(function () {
            size = 20;
            page = 1;
            $("#page button:first").text("X20");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        $("#page a:eq(2)").click(function () {
            size = 30;
            page = 1;
            $("#page button:first").text("X30");
            query();
            var $page = $("#page span:eq(1) button:gt(0):lt(5)");
            for (let i = 0; i < 5; i++) {
                $($page[i]).text(i + 1);
            }
        })
        //上一页
        $("#page span:eq(1) button:first").click(function () {
            var lask = Math.ceil(total / size)
            if (page > 1) {
                page = page - 1;
                query();
                if (page > 2 && page < lask - 2) {
                    var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                    for (let i = 0; i < 5; i++) {
                        $($page[i]).text(parseInt($($page[i]).text()) - 1);
                    }
                }
            }
        })
        //下一页
        $("#page span:eq(1) button:last").click(function () {
            var lask = Math.ceil(total / size)
            if (page < lask) {
                page = page + 1;
                query();
                if (page > 3 && page < lask - 1) {
                    var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                    for (let i = 0; i < 5; i++) {
                        $($page[i]).text(parseInt($($page[i]).text()) + 1);
                    }
                }
            }
        })
        //点击跳转
        $("#page span:eq(1) button:gt(0):lt(5)").click(function () {
            page = $(this).text();
            if (page > Math.ceil(total / size)) {
                page = Math.ceil(total / size);
            }
            query();
            page_skip();
        })
        //输入跳转
        $("#page span:eq(2) button").click(function () {
            page = $("#page span:eq(2) input").val();
            if (page > Math.ceil(total / size)) {
                page = Math.ceil(total / size);
            }
            if (page < 1){
                page = 1;
            }
            query();
            page_skip();
        })
        // 页面跳转下面的数字
        function page_skip() {
            var lask = Math.ceil(total / size);
            if (lask > 5) {
                if (page <= 3) {
                    var count = 1;
                } else if (page >= lask - 2) {
                    var count = lask - 4;
                } else {
                    var count = page - 2;
                }
                var $page = $("#page span:eq(1) button:gt(0):lt(5)");
                for (let i = 0; i < 5; i++) {
                    $($page[i]).text(count++);
                }
            }
        }

    })
})