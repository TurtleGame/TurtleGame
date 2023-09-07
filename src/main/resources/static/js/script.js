function showAlert() {
    var alertMessage = document.getElementById("alertMessage");
    alertMessage.classList.remove("hidden");
}

function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");
    const header = document.getElementById("form-header")



    if (loginForm.style.display === "none") {
        loginForm.style.display = "block";
        registerForm.style.display = "none";

        header.textContent = 'Zaloguj';

    } else {
        loginForm.style.display = "none";
        registerForm.style.display = "block";
        header.textContent = 'Zarejestruj';

    }
}