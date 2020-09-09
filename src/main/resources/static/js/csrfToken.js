var token
var headerName
document.addEventListener("DOMContentLoaded", function () {
    token = $("meta[name='_csrf']").attr("content");
    headerName = $("meta[name='_csrf_header']").attr("content");
})