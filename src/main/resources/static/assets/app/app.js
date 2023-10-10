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

    const receivedHeader = document.getElementById('received-header');
    const sentHeader = document.getElementById('sent-header');
    const receivedMessage = document.getElementById('received-message');
    const sentMessage = document.getElementById('sent-message');
    const createHeader = document.getElementById('create-header');
    const createMessage = document.getElementById('create-message');

    receivedHeader?.addEventListener('click', function () {
        receivedMessage.style.display = 'block';
        sentMessage.style.display = 'none';
        receivedHeader.classList.add('active-header');
        sentHeader.classList.remove('active-header');
        createHeader.classList.remove('active-header');
        createMessage.style.display= 'none';

    });
    sentHeader?.addEventListener('click', function () {
        receivedMessage.style.display = 'none';
        sentMessage.style.display = 'block';
        sentHeader.classList.add('active-header');
        receivedHeader.classList.remove('active-header');
        createHeader.classList.remove('active-header');
        createMessage.style.display= 'none';
    });
    createHeader?.addEventListener('click', function (){
        createHeader.classList.add('active-header');
        receivedHeader.classList.remove('active-header');
        sentHeader.classList.remove('active-header');
        createMessage.style.display= 'block';
        receivedMessage.style.display = 'none';
        sentMessage.style.display = 'none';

    });

    $('.select2').select2({
        language: "pl",
        ajax: {
            url: 'user/search-by-keyword',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    keyword: params.term
                };
            },
            processResults: function (data) {
                return {
                    results: data.map(function (item) {
                        return {id: item, text: item}
                    })
                };
            },
            cache: true
        },
        minimumInputLength: 2
    });


});


async function readMessage(element) {
    const messageId = element.getAttribute('data-message-id');
    const response = await fetch(`/private-message/${messageId}/read`, {
            method: "POST"
        }
    );

    if (response.status === 204) {
        element.classList.remove('message-unread');
        element.classList.add('message-read');
    }

    const numberOfUnreadMessages = document.querySelectorAll('.message-unread').length;
    if (numberOfUnreadMessages === 0) {
        document.querySelector('.icon-messages').classList.remove('fa-bounce');
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

