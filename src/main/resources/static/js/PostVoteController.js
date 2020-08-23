const toggles = document.querySelectorAll('[aria-pressed]');
var voteCount = document.getElementById("voteCount");
var voteNum = parseInt(voteCount.textContent);
var upVoteButton = document.getElementById("upVoteButton");
var downVoteButton = document.getElementById("downVoteButton");

toggles.forEach(toggle => {
    toggle.addEventListener("click", (e) => {
        let pressed = e.target.getAttribute("aria-pressed") === "true";
        e.target.setAttribute("aria-pressed", String(!pressed));
    })
})

function upVote() {
    if(upVoteButton.getAttribute("aria-pressed") !== "true") {
        //turning toggle on
        voteCount.textContent = (voteNum + 1).toString();
        downVoteButton.setAttribute("aria-pressed", String(false));
    }
    else {
        //turning toggle off
        voteCount.textContent = voteNum.toString();
    }
}

function downVote() {
    if(downVoteButton.getAttribute("aria-pressed") !== "true") {
        //turning toggle on
        voteCount.textContent = (voteNum - 1).toString();
        upVoteButton.setAttribute("aria-pressed", String(false));
    }
    else {
        //turning toggle off
        voteCount.textContent = voteNum.toString();
    }
}