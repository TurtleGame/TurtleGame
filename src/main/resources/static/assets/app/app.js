function switchForms() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("registration-form");

    loginForm.classList.toggle('hidden');
    registerForm.classList.toggle('hidden');
}

function switchFormsMessage() {
    const receivedHeader = document.getElementById('received-header');
    const sentHeader = document.getElementById('sent-header');
    const receivedMessage = document.getElementById('received-message');
    const sentMessage = document.getElementById('sent-message');

    receivedHeader.addEventListener('click', function () {
        receivedMessage.style.display = 'block';
        sentMessage.style.display = 'none';
        receivedHeader.classList.add('active-header');
        sentHeader.classList.remove('active-header');
    });

    sentHeader.addEventListener('click', function () {
        receivedMessage.style.display = 'none';
        sentMessage.style.display = 'block';
        sentHeader.classList.add('active-header');
        receivedHeader.classList.remove('active-header');
    });
}

function abandonTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz porzucić tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function deleteMessage(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz usunąć tą wiadomość?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {

    document.querySelectorAll('.message-title').forEach((el) => {
        el.addEventListener('click', () => {
            el.closest('.message-container')
                .querySelector(".message-content")
                .classList
                .toggle('hidden');
        });
    });

});

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