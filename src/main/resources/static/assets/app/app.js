function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");

    loginForm.classList.toggle('hidden');
    registerForm.classList.toggle('hidden');
}

function abandonTurtleConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        var confirmation = confirm("Czy na pewno chcesz porzucić tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function adoptEggConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        var confirmation = confirm("Czy na pewno chcesz zaadoptować to jajko?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function openForm() {
    document.getElementById("myForm").style.display = "block";
}

function closeForm() {
    document.getElementById("myForm").style.display = "none";
}