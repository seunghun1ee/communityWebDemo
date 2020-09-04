var subscribeButton = document.getElementById("subscribeButton");

document.addEventListener("DOMContentLoaded", function () {
    if(subscribeButton !== null) {
        var request
        if(window.location.href.charAt(window.location.href.length - 1) === "/") {
            request = "checkSubscribers";
        }
        else {
            request = "/checkSubscribers";
        }
        $.post({
            url: window.location.href + request,
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
        subscribeButton.classList.add("active");
        subscribeButton.setAttribute("aria-pressed",String(true));
        subscribeButton.textContent = "Subscribed";
    }
    else {
        subscribeButton.classList.remove("active");
        subscribeButton.setAttribute("aria-pressed",String(false));
        subscribeButton.textContent = "Subscribe";
    }
}