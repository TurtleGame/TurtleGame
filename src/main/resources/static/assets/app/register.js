function switchForms(target) {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");
    const remindPasswordForm = document.getElementById("remind-password-form");

    loginForm.classList.add('hidden');
    registerForm.classList.add('hidden');
    remindPasswordForm.classList.add('hidden');
    document.getElementById(target).classList.remove('hidden');
}

document.addEventListener("DOMContentLoaded", () => {
    $('.closeButton').on('click', function () {
        const message = $(this).closest('.errorMessage, .successMessage');
        console.log(message);
        message.hide();
    });
});