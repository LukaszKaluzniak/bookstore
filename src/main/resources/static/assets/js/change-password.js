$('document').ready(function() {

    var password = document.getElementById("floatingNewPassword");
    var confirmPassword = document.getElementById("floatingNewPasswordConfirmation");

    function validatePassword() {
        if(password.value != confirmPassword.value) {
            confirmPassword.setCustomValidity("Wprowadzone hasła nie są takie same!");
        } else {
            confirmPassword.setCustomValidity('');
        }
    }

    password.onchange = validatePassword;
    confirmPassword.onkeyup = validatePassword;

});