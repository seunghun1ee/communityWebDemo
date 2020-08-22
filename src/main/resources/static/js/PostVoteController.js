var initialVote = document.getElementById("voteCount");
var initialVoteNum = parseInt(initialVote.textContent);

function upVote() {
    var voteCount = document.getElementById("voteCount");
    let voteNum = parseInt(voteCount.textContent);
    if(voteNum < initialVoteNum + 1) {
        voteCount.textContent = (voteNum + 1).toString();
    }

}

function downVote() {
    var voteCount = document.getElementById("voteCount");
    let voteNum = parseInt(voteCount.textContent);
    if(voteNum > initialVoteNum - 1) {
        voteCount.textContent = (voteNum - 1).toString();
    }

}