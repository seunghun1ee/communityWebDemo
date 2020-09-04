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
            url:window.location.href + subRequest +"?sub=true",
            cache: false,
            success: function (response) {
                if(response) {
                    subscribeButton.classList.add("active");
                    subscribeButton.setAttribute("aria-pressed",String(true));
                    subscribeButton.textContent = "Subscribed";
                }
            },
            statusCode: {
                400: function () {
                    alert("You already subscribed to this thread");
                    location.reload();
                },
                403: function () {
                    alert("Illegal subscription request");
                    location.reload();
                },
                404: function () {
                    alert("Thread not found. Press OK to go back to homepage.");
                    window.location.href = "/";
                }
            }
        });
    }
    else {
        $.post({
            url:window.location.href + subRequest + "?sub=false",
            cache: false,
            success: function (response) {
                if(response) {
                    subscribeButton.classList.remove("active");
                    subscribeButton.setAttribute("aria-pressed",String(false));
                    subscribeButton.textContent = "Subscribe";
                }
            },
            statusCode: {
                400: function () {
                    alert("You are not subscribed to this thread");
                    location.reload();
                },
                403: function () {
                    alert("Illegal subscription request");
                    location.reload();
                },
                404: function () {
                    alert("Thread not found. Press OK to go back to homepage.");
                    window.location.href = "/";
                }
            }
        });
    }
}