$('document').ready(function() {

    var password = document.getElementById("floatingPassword");
    var confirmPassword = document.getElementById("floatingPasswordConfirmation");

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