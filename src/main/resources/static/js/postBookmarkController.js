var bookMarkButton = document.getElementById("bookmarkButton");

document.addEventListener("DOMContentLoaded", function () {
    if(bookMarkButton !== null && bookMarkButton.getAttribute("aria-pressed") === "true") {
        bookMarkButton.classList.add("active");
        bookMarkButton.setAttribute("aria-pressed",String(true));
        bookMarkButton.textContent = "Bookmarked";
    }
});

function bookmark() {
    if(bookMarkButton.getAttribute("aria-pressed") !== "true") {
        $.post({
            url:window.location.href + "/bookmark?mode=true",
            cache:false,
            success: function (response) {
                if(response) {
                    bookMarkButton.classList.add("active");
                    bookMarkButton.setAttribute("aria-pressed",String(true));
                    bookMarkButton.textContent = "Bookmarked";
                }
            }
        })
    }
    else {
        $.post({
            url:window.location.href + "/bookmark?mode=false",
            cache:false,
            success: function (response) {
                if(response) {
                    bookMarkButton.classList.remove("active");
                    bookMarkButton.setAttribute("aria-pressed",String(false));
                    bookMarkButton.textContent = "Bookmark";
                }
            }
        })
    }
}