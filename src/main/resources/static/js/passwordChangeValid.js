var inputNewPassword = document.getElementById("inputNewPassword");
var inputRepeatPassword = document.getElementById("inputRepeatPassword");

function validatePasswordChange() {
    if(inputNewPassword.value !== inputRepeatPassword.value) {
        inputRepeatPassword.setCustomValidity("Passwords don't match");
    }
    else {
        inputRepeatPassword.setCustomValidity("");
    }
}

inputNewPassword.onchange = validatePasswordChange;
inputNewPassword.onkeyup = validatePasswordChange;
inputRepeatPassword.onchange = validatePasswordChange;
inputRepeatPassword.onkeyup = validatePasswordChange;