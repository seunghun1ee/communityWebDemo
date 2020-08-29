var voteCount = document.getElementById("voteCount");
let voteNum = parseInt(voteCount.textContent);
var upVoteButton = document.getElementById("upVoteButton");
var downVoteButton = document.getElementById("downVoteButton");

function upVote() {
    $.post({
        url: window.location.href + "/checkVoteBefore?isUpvote=true",
        success: function (response) {
            if(response) {
                alert("You already voted");
            }
            else {
                if(upVoteButton.getAttribute("aria-pressed") !== "true") {
                    upVoteButton.setAttribute("aria-pressed", String(true));
                    voteCount.textContent = (voteNum + 1).toString();
                    downVoteButton.setAttribute("aria-pressed", String(false));
                    submit();
                    //update voteNum
                    voteNum = parseInt(voteCount.textContent);
                }
                else {
                    upVoteButton.setAttribute("aria-pressed", String(false));
                    voteCount.textContent = voteNum.toString();
                }
            }
        }
    });
}

function downVote() {
    $.post({
        url: window.location.href + "/checkVoteBefore?isUpvote=false",
        success: function (response) {
            if(response) {
                alert("You already voted");
            }
            else {
                if(downVoteButton.getAttribute("aria-pressed") !== "true") {
                    downVoteButton.setAttribute("aria-pressed", String(true));
                    voteCount.textContent = (voteNum - 1).toString();
                    upVoteButton.setAttribute("aria-pressed", String(false));
                    submit();
                    //Update voteNum
                    voteNum = parseInt(voteCount.textContent);
                }
                else {
                    downVoteButton.setAttribute("aria-pressed", String(false));
                    voteCount.textContent = voteNum.toString();
                }
            }
        }
    });
}

function submit() {
    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        $.post({
            url: window.location.href + "/vote?type=upvote",
            cache: false
        });
    }
    else if(downVoteButton.getAttribute("aria-pressed") === "true") {
        $.post({
            url: window.location.href + "/vote?type=downvote",
            cache: false
        });
    }
}

function cancelVote() {
    $.post({
        url: window.location.href + "/cancelVote",
        cache: false
    })
}