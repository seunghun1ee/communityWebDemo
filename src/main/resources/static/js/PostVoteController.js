var voteCount = document.getElementById("voteCount");
let voteNum = parseInt(voteCount.textContent);
var upVoteButton = document.getElementById("upVoteButton");
var downVoteButton = document.getElementById("downVoteButton");

var numList = [];
document.addEventListener("DOMContentLoaded", function () {

    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        numList[0] = (voteNum - 2).toString();
        numList[1] = (voteNum - 1).toString();
        numList[2] = voteNum.toString();
    }
    else if(downVoteButton.getAttribute("aria-pressed") === "true") {
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

function doUpVote() {
    if(downVoteButton.getAttribute("aria-pressed") === "true") {
        submitDownToUp();
    }
    else {
        submitUpVote();
    }
    upVoteButton.setAttribute("aria-pressed", String(true));
    downVoteButton.setAttribute("aria-pressed", String(false));
    voteCount.textContent = numList[2];
}

function cancelUpVote() {
    upVoteButton.setAttribute("aria-pressed", String(false));
    voteCount.textContent = numList[1];
    cancelVote();
}

function doDownVote() {
    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        submitUpToDown();
    }
    else {
        submitDownVote();
    }
    downVoteButton.setAttribute("aria-pressed", String(true));
    upVoteButton.setAttribute("aria-pressed", String(false));
    voteCount.textContent = numList[0];
}

function cancelDownVote() {
    downVoteButton.setAttribute("aria-pressed", String(false));
    voteCount.textContent = numList[1];
    cancelVote();
}

function submitUpVote() {
    $.post({
        url: window.location.href + "/vote?type=upvote",
        cache: false
    });
}

function submitDownVote() {
    $.post({
        url: window.location.href + "/vote?type=downvote",
        cache: false
    });
}

function cancelVote() {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false
    })
}

function submitDownToUp() {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false,
        success: function (response) {
            if(response === "success") {
                submitUpVote();
            }
        }
    })
}

function submitUpToDown() {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false,
        success: function (response) {
            if(response === "success") {
                submitDownVote();
            }
        }
    })
}