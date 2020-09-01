function addComment() {
    var message = document.getElementById("inputMessage");
    var password = document.getElementById("inputPassword");

    var comment = {
        "message": message?.value,
        "password": password?.value
    }
    $.post({
        url: window.location.href + "/new_comment",
        data: JSON.stringify(comment),
        cache: false,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if(response) {
                location.reload();
            }
            else {
                alert("Error");
                window.location.href = "/";
            }
        },
        statusCode: {
            404: function () {
                alert("The post was deleted. Press OK to go back to the thread.");
                window.location.href = "../";
            },
            500: function () {
                alert("The thread was deleted. Press OK to go back to the homepage.");
                window.location.href = "/";
            }
        }
    });
}

function replyComment(postId, commentId) {
    var message = document.getElementById("inputReplyMessage"+commentId);
    var password = document.getElementById("inputReplyPassword"+commentId);
    var comment = {
        "message": message?.value,
        "password": password?.value
    }
    $.post({
        url: "/posts/"+postId+"/comments/"+commentId+"/reply",
        data: JSON.stringify(comment),
        cache: false,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if(response) {
                location.reload();
            }
            else {
                alert("Error");
                window.location.href = "/";
            }
        }
    });
}

function deleteComment(postId, commentId) {
    var commentPassword = document.getElementById("inputCommentPassword"+commentId);
    var password = {
        "password": commentPassword?.value
    };

    $.post({
        url: "/posts/"+postId+"/comments/"+commentId +"/delete",
        data: JSON.stringify(password),
        cache: false,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if(response) {
                location.reload();
            }
            else {
                alert("Error");
                window.location.href = "/";
            }
        }
    });
}