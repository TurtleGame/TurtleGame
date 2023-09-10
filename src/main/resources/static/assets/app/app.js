function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");

    loginForm.classList.toggle('hidden');
    registerForm.classList.toggle('hidden');
}

function abandonTurtleConfirm(buttonElement) {
    var turtleId = buttonElement.getAttribute("data-turtle-id");
    if (confirm("Czy na pewno chcesz porzucić żółwia?")) {
        window.location.href = "/turtles/" + turtleId + "/delete";
    }
}
