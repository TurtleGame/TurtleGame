function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");

    loginForm.classList.toggle('hidden');
    registerForm.classList.toggle('hidden');
}

function abandonTurtleConfirm(buttonElement) {
    document.getElementById("deleteTurtleForm").addEventListener("submit", function(event) {
        var confirmation = confirm("Czy na pewno chcesz porzucić tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    });
}
