// function post() {
//     var questionId = $("#question_id").val();
//     var content = $("#comment_content").val();
//
//     if (!content) {
//         alert("不能回复空");
//     }
//
//     $.ajax({
//         type: "POST",
//         url: "/comment",
//         // 请求参数的格式
//         contentType: 'application/json',
//         // '{"parentId": 1,"content": "hello, world","type": 1}'
//         data: JSON.stringify({
//             "parentId": questionId,
//             "content": content,
//             "type": 1
//         }),
//         success: function (response) {
//             if (response.code == 200) {
//                 $("#comment_content").val("");
//                 location.reload();
//             } else {
//                 // 如果用户未登录
//                 if (response.code == 2003) {
//                     var isAccept = confirm(response.message);
//                     if (isAccept) {
//                         $('#myModal').modal({});
//                         // 打开一个新的页面
//                         // window.open("https://github.com/login/oauth/authorize?client_id=7b8e0403167f8fef70f3&redirect_uri=http://localhost:8887/callback/github&scope=user&state=1")
//                         // window.localStorage.setItem("closable","true");
//                     }
//
//                 }
//             }
//         },
//         // success回调函数的response参数的格式
//         dataType: "json"
//     });
// }

var addCookie = function (name, value) {
    document.cookie = name + "=" + value + ";path=/";
};


//抽取方法
/**
 * @param parentId 父级id
 * @param type 当前评论的级别（一级评论还是二级评论）
 * @param content 评论的内容
 */
function comment2Parent(parentId, type, content) {
    // 如果content为nan或为全空格
    if (!content || content.match(/^[ ]*$/)) {
        alert("不能回复空内容~~~");
        return;
    }
    $.ajax(
        {
            type: "post",
            url: "/comment",
            contentType: 'application/json',
            data: JSON.stringify({
                "parentId": parentId,
                "content": content,
                "type": type
            }),
            success: function (response) {
                if (response.code == 200) {
                    $("#comment_content").val("");
                    location.reload();
                } else {
                    // 需要登录
                    if (response.code == 2003) {
                        var isAccept = confirm(response.message);
                        // 如果接受登录
                        if (isAccept) {
                            // 添加cookie
                            // 把评论的内容添加到cookie中
                            addCookie("noLoginComment",content);
                            // 如果是一级评论
                            if (type == 1) {
                                // parentId就相当于问题id
                                addCookie("commentButNoLogin", parentId);
                            }// 如果是二级评论
                            else {
                                // 如果是二级评论，就要从input元素中获取问题id
                                var questionId = $("#question_id")[0].value;
                                addCookie("commentButNoLogin",questionId);
                            }

                            // 打开登录界面
                            $('#myModal').modal({});
                            // window.open("https://github.com/login/oauth/authorize?client_id=cd40e3e26ced2f5e81d0&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                            // window.open("https://github.com/login/oauth/authorize");
                            // window.localStorage.setItem("closeable", "true")
                        }
                    } else {
                        alert(response.message);
                    }
                }
                console.log(response);
            },
            dataType: "json"
        }
    )
}

/*提交回复*/
function post() {
    var questionId = $("#question_id")[0].value;
    var commentContent = $("#comment_content").val();
    comment2Parent(questionId, 1, commentContent);
}

/*提交二级评论*/
function comment(e) {
    // 获取一级评论的id
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2Parent(commentId, 2, content);
}

// 是否已经点开了一个二级评论框
var opening = false;

// 上一个点击的二级评论的弹窗Dom对象
var pre;

// 上一个点击的评论图标对象
var preCommentSpan;

/*展开二级评论*/
function collapseComments(e) {

    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");

    // 折叠二级评论
    if (collapse) {
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } // 展开二级评论
    else {
        debugger;
        // 是否已经点开了一个二级评论框
        // 如果打开了，就关闭已经打开的二级评论框（为了保证只有一个二级评论框展示）
        if (opening) {
            pre.removeClass("in");
            preCommentSpan.removeAttribute("data-collapse");
            preCommentSpan.classList.remove("active");
            opening = false;
        }
        var subCommentContainer = $("#comment-" + id);
        // 如果没有评论的话
        if (subCommentContainer.children().length != 1) {
            // 展开二级评论
            comments.addClass("in");
            // 标记二级评论为展开状态
            e.setAttribute("data-collapse", true);
            e.classList.add("active");
            $("#input-" + id).val("");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        // moment.js工具格式化
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media comment-separate"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                // 展开二级评论
                comments.addClass("in");


                // 把当前的评论图标元素设置为打开状态
                e.setAttribute("data-collapse", true);
                // 把当前的评论图标元素设置为选中状态
                e.classList.add("active");
                $("#input-" + id).val("");
            });
        }
    }
    // 设置已经打开了一个二级评论弹窗
    opening = !collapse;
    // 当前二级评论弹窗元素就是下一个元素的前一个元素
    pre = comments;
    // 当前二级评论图标元素就是下一个元素的前一个元素
    preCommentSpan = e;
}