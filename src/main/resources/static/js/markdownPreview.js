var md
var inputBody = document.getElementById("inputBody");
var preview = document.getElementById("preview");

function htmlToElements(html) {
    var template = document.createElement('template');
    template.innerHTML = html;
    return template.content.childNodes;
}

document.addEventListener("DOMContentLoaded", function () {
    md = window.markdownit('commonmark',{breaks: true});
    updatePreview();
})

function updatePreview() {
    var result = md.render(inputBody.value);
    while (preview.lastElementChild) {
        preview.removeChild(preview.lastElementChild);
    }
    var nodes = htmlToElements(result)
    for(let i = 0; i < nodes.length; i++) {
        preview.appendChild(nodes[i]);
    }

}

inputBody.onkeyup = updatePreview;
inputBody.onchange = updatePreview;

