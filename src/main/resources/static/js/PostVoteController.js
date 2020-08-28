const toggles = document.querySelectorAll('[aria-pressed]');
var voteCount = document.getElementById("voteCount");
var voteNum = parseInt(voteCount.textContent);
var upVoteButton = document.getElementById("upVoteButton");
var downVoteButton = document.getElementById("downVoteButton");

function upVote() {
    $.post({
        url: window.location.href + "/checkVoteBefore",
        success: function (response) {
            if(response) {
                alert("You already voted");
            }
            else {
                if(upVoteButton.getAttribute("aria-pressed") !== "true") {
                    upVoteButton.setAttribute("aria-pressed", String(true));
                    voteCount.textContent = (voteNum + 1).toString();
                    downVoteButton.setAttribute("aria-pressed", String(false));
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
        url: window.location.href + "/checkVoteBefore",
        success: function (response) {
            if(response) {
                alert("You already voted");
            }
            else {
                if(downVoteButton.getAttribute("aria-pressed") !== "true") {
                    downVoteButton.setAttribute("aria-pressed", String(true));
                    voteCount.textContent = (voteNum - 1).toString();
                    upVoteButton.setAttribute("aria-pressed", String(false));
                }
                else {
                    downVoteButton.setAttribute("aria-pressed", String(false));
                    voteCount.textContent = voteNum.toString();
                }
            }
        }
    });
}

// window.onbeforeunload = function () {
//     if(upVoteButton.getAttribute("aria-pressed") === "true") {
//         $.post({
//             url: window.location.href + "/vote/upvote",
//             cache: false
//         });
//     }
//     else if(downVoteButton.getAttribute("aria-pressed") === "true") {
//         $.post({
//             url: window.location.href + "/vote/downvote",
//             cache: false
//         });
//     }
// }

function submit() {
    if(upVoteButton.getAttribute("aria-pressed") === "true") {
        $.post({
            url: window.location.href + "/vote/upvote",
            cache: false
        });
    }
    else if(downVoteButton.getAttribute("aria-pressed") === "true") {
        $.post({
            url: window.location.href + "/vote/downvote",
            cache: false
        });
    }
}