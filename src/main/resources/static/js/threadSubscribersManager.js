document.addEventListener("DOMContentLoaded", function () {
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
                console.log("subscribed");
            }
            else {
                console.log("not subscribed");
            }
        }
    })
});