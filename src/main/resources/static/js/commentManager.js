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
            404: function (response) {
                notFound(response);
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
        url: window.location.href+"/comments/"+commentId +"/reply",
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
            404: function (response) {
                notFound(response);
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
        url: window.location.href+"/comments/"+commentId +"/delete",
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
        },
        statusCode: {
            400:function () {
                alert("The password for the comment is incorrect.");
            },
            403: function () {
                alert("Access denied.");
            },
            404: function (response) {
                notFound(response);
            }
        }
    });
}

function notFound(response) {
    let errorMessage = response?.responseJSON?.message;
    switch (errorMessage) {
        case "no_thread":
            alert("The thread does not exist. Press OK to return to homepage.");
            window.location.href = "/";
            break;
        case "no_post":
            alert("The post does not exist. Press OK to return to the thread.");
            window.location.href = "../";
            break;
        case "no_comment":
            alert("The comment does not exist. Press OK to refresh the page.");
            location.reload();
            break;
        default:
            alert("Error");
            break;
    }
}