function checkIfClass(item) {
        return item.classList.length <= 0;
}

function setClassToItem(item, className) {
        item.className = className;
}

function imgFluid() {
    var imgs = document.getElementsByTagName("img");
    for(let i=0; i < imgs.length; i++) {
        if(checkIfClass(imgs[i])) {
            setClassToItem(imgs[i], "img-fluid");
        }
    }
}
function blockquote() {
    var blockquotes = document.getElementsByTagName("blockquote");
    for(let i=0; i < blockquotes.length; i++) {
        setClassToItem(blockquotes[i], "font-italic blockquote");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    imgFluid();
    blockquote();
})