function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");

    loginForm.classList.toggle('hidden');
    registerForm.classList.toggle('hidden');
}


document.addEventListener("DOMContentLoaded", () => {
    $('.closeButton').on('click', function () {
        const message = $(this).closest('.errorMessage, .successMessage');
        console.log(message);
        message.hide();
    });
});