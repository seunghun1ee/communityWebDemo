function upVote() {
    var voteCount = document.getElementById("voteCount");
    let voteNum = parseInt(voteCount.textContent);
    voteCount.textContent = (voteNum + 1).toString();
}

function downVote() {
    var voteCount = document.getElementById("voteCount");
    let voteNum = parseInt(voteCount.textContent);
    voteCount.textContent = (voteNum - 1).toString();
}