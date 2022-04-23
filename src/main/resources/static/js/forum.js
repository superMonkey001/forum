function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    $.ajax({
        type: "POST",
        url: "/comment",
        // 请求参数的格式
        contentType: 'application/json',
        // '{"parentId": 1,"content": "hello, world","type": 1}'
        data: JSON.stringify({
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code == 200) {
                $("#comment_section").hide();
            } else {
                // 如果用户未登录
                if (response.code == 2003) {
                    var isAccept = confirm(response.message);
                    if (isAccept) {
                        // 打开一个新的页面
                        window.open("https://github.com/login/oauth/authorize?client_id=7b8e0403167f8fef70f3&redirect_uri=http://localhost:8887/callback&scope=user&state=1")
                        window.localStorage.setItem("closable","true");
                    }

                }
            }
        },
        // success回调函数的response参数的格式
        dataType: "json"
    });
}

//抽取方法
function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }
    $.ajax(
        {
            type: "post",
            url: "/comment",
            contentType: 'application/json',
            data: JSON.stringify({
                "parentId": targetId,
                "content": content,
                "type": type
            }),
            success: function (param) {
                if (param.code == 200) {
                    $("#comment_content").val("");
                    location.reload();
                } else {
                    if (param.code == 2003) {
                        var isAccept = confirm(param.message);
                        if (isAccept) {
                            window.open("https://github.com/login/oauth/authorize?client_id=cd40e3e26ced2f5e81d0&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                            // window.open("https://github.com/login/oauth/authorize");
                            window.localStorage.setItem("closeable", "true")
                        }
                    } else {
                        alert(param.message);
                    }
                }
                console.log(param);
            },
            dataType: "json"
        }
    )
}