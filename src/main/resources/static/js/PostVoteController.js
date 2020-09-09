var voteCount = document.getElementById("voteCount");
let voteNum = parseInt(voteCount.textContent);
var upVoteButton = document.getElementById("upVoteButton");
var downVoteButton = document.getElementById("downVoteButton");

var numList = [];
document.addEventListener("DOMContentLoaded", function () {

    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        upVoteButton.classList.add("active");
        numList[0] = (voteNum - 2).toString();
        numList[1] = (voteNum - 1).toString();
        numList[2] = voteNum.toString();
    }
    else if(downVoteButton.getAttribute("aria-pressed") === "true") {
        downVoteButton.classList.add("active");
        numList[0] = voteNum.toString();
        numList[1] = (voteNum + 1).toString();
        numList[2] = (voteNum + 2).toString();
    }
    else {
        numList[0] = (voteNum - 1).toString();
        numList[1] = voteNum.toString();
        numList[2] = (voteNum + 1).toString();
    }
})

function upVote() {
    if(upVoteButton.getAttribute("aria-pressed") !== "true") {
        $.post({
            url: window.location.href + "/checkVoteBefore?isUpvote=true",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(headerName, token);
            },
            success: function (response) {
                if(response) {
                    alert("You already voted");
                }
                else {
                    doUpVote();
                }
            }
        })
    }
    else {
        cancelUpVote()
    }
}

function downVote() {
    if(downVoteButton.getAttribute("aria-pressed") !== "true") {
        $.post({
            url: window.location.href + "/checkVoteBefore?isUpvote=false",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(headerName, token);
            },
            success: function (response) {
                if(response) {
                    alert("You already voted");
                }
                else {
                    doDownVote();
                }
            }
        });
    }
    else {
        cancelDownVote();
    }
}

function up() {
    voteCount.textContent = numList[2];
    upVoteButton.setAttribute("aria-pressed", String(true));
    upVoteButton.classList.add("active");
    downVoteButton.setAttribute("aria-pressed", String(false));
    downVoteButton.classList.remove("active");
}

function down() {
    voteCount.textContent = numList[0];
    downVoteButton.setAttribute("aria-pressed", String(true));
    downVoteButton.classList.add("active");
    upVoteButton.setAttribute("aria-pressed", String(false));
    upVoteButton.classList.remove("active");
}

function doUpVote() {
    if(downVoteButton.getAttribute("aria-pressed") === "true") {
        up();
        reverseVote("upvote");
    }
    else {
        up();
        submitVote("upvote");
    }
}

function cancelUpVote() {
    voteCount.textContent = numList[1];
    upVoteButton.setAttribute("aria-pressed", String(false));
    upVoteButton.classList.remove("active");
    cancelVote();
}

function doDownVote() {
    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        down();
        reverseVote("downvote");
    }
    else {
        down();
        submitVote("downvote")
    }
}

function cancelDownVote() {
    voteCount.textContent = numList[1];
    downVoteButton.setAttribute("aria-pressed", String(false));
    downVoteButton.classList.remove("active");
    cancelVote();
}

function submitVote(type) {
    $.post({
        url: window.location.href + "/vote?type=" + type,
        cache: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(headerName, token);
        }
    });
}

function cancelVote() {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(headerName, token);
        }
    })
}

function reverseVote(type) {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(headerName, token);
        },
        success: function (response) {
            if(response === "success") {
                submitVote(type);
            }
        }
    })
}