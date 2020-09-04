var subscribeButton = document.getElementById("subscribeButton");
var checkSubRequest
var subRequest
document.addEventListener("DOMContentLoaded", function () {
    if(subscribeButton !== null) {

        if(window.location.href.charAt(window.location.href.length - 1) === "/") {
            checkSubRequest = "checkSubscribers";
            subRequest = "subscribe";
        }
        else {
            checkSubRequest = "/checkSubscribers";
            subRequest = "/subscribe";
        }
        $.post({
            url: window.location.href + checkSubRequest,
            cache:false,
            success: function (response) {
                if(response) {
                    subscribeButton.classList.add("active");
                    subscribeButton.setAttribute("aria-pressed",String(true));
                    subscribeButton.textContent = "Subscribed";
                }
            }
        })
    }
});

function toggleSub() {
    if(subscribeButton.getAttribute("aria-pressed") !== "true") {
        $.post({
            url:window.location.href + subRequest,
            cache: false,
            success: function (response) {
                if(response) {
                    subscribeButton.classList.add("active");
                    subscribeButton.setAttribute("aria-pressed",String(true));
                    subscribeButton.textContent = "Subscribed";
                }
            }
        })
    }
    else {
        subscribeButton.classList.remove("active");
        subscribeButton.setAttribute("aria-pressed",String(false));
        subscribeButton.textContent = "Subscribe";
    }
}