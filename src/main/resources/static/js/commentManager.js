function addComment() {
    var message = document.getElementById("inputMessage");
    var password = document.getElementById("inputPassword");

    var comment = {
        "message": message?.value.toString(),
        "password": password?.value.toString()
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
        }
    })
}

function deleteComment(postId, commentId) {
    var commentPassword = document.getElementById("inputCommentPassword"+commentId);
    var password = {
        "password": commentPassword?.value.toString()
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
    })
}