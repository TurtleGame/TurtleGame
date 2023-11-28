function abandonTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz porzucić tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function sellTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz sprzedać tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function buyTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz kupić tego żółwia?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function undoTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz wycofać tego żółwia ze sprzedaży?");
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

    updateCountdown();

    document.querySelectorAll('.message-title').forEach((el) => {
        el.addEventListener('click', () => {
            el.closest('.message-container')
                .querySelector(".message-content")
                .classList
                .toggle('hidden');
        });


    });

    $(".new-news-button").click(function () {
        $(".new-news-content").toggleClass("hidden");
    })

    if ($('.new-news-button').length) {
        CKEDITOR.replace('content');
    }


    const receivedHeader = document.getElementById('received-header');
    const sentHeader = document.getElementById('sent-header');
    const receivedMessage = document.getElementById('received-message');
    const sentMessage = document.getElementById('sent-message');
    const createHeader = document.getElementById('create-header');
    const createMessage = document.getElementById('create-message');
    const readAllButton = document.getElementById('read-all');
    const turtlesHeader = document.getElementById('turtles-header');
    const itemsHeader = document.getElementById('items-header');
    const turtlesSelling = document.getElementById('turtles-selling');
    const itemsSelling = document.getElementById('items-selling');

    turtlesHeader?.addEventListener('click', function () {
        turtlesSelling.style.display = 'block';
        itemsSelling.style.display = 'none';
        turtlesHeader.classList.add('active-header');
        itemsHeader.classList.remove('active-header');
    });
    itemsHeader?.addEventListener('click', function () {
        itemsSelling.style.display = 'block';
        turtlesSelling.style.display = 'none';
        itemsHeader.classList.add('active-header');
        turtlesHeader.classList.remove('active-header');
    });

    receivedHeader?.addEventListener('click', function () {
        receivedMessage.style.display = 'block';
        sentMessage.style.display = 'none';
        receivedHeader.classList.add('active-header');
        sentHeader.classList.remove('active-header');
        createHeader.classList.remove('active-header');
        createMessage.style.display = 'none';
        readAllButton.style.display = 'block';

    });
    sentHeader?.addEventListener('click', function () {
        receivedMessage.style.display = 'none';
        sentMessage.style.display = 'block';
        sentHeader.classList.add('active-header');
        receivedHeader.classList.remove('active-header');
        createHeader.classList.remove('active-header');
        createMessage.style.display = 'none';
        readAllButton.style.display = 'none';
    });
    $('#create-header').on('click', function () {
        createHeader.classList.add('active-header');
        receivedHeader.classList.remove('active-header');
        sentHeader.classList.remove('active-header');
        createMessage.style.display = 'block';
        receivedMessage.style.display = 'none';
        sentMessage.style.display = 'none';
        readAllButton.style.display = 'none';
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

    if ($('#create-header').length) {

        const urlParams = new URLSearchParams(window.location.search);
        const recipient = urlParams.get('recipient');
        if (recipient) {
            $('#create-header').trigger('click');
            const newOption = new Option(recipient, recipient, true, true);
            $('[name="recipient"]').append(newOption).trigger('change');
        }

        $('.reply-button').on('click', function () {
            const sender = $(this).data('sender');
            const title = $(this).data('title');
            $('#create-header').trigger('click');
            $('[name="title"]', $('#create-message')).val("Re: " + title);
            const newOption = new Option(sender, sender, true, true);
            $('[name="recipient"]').append(newOption).trigger('change');
        })
    }

    $('.closeButton').on('click', function () {
        const message = $(this).closest('.errorMessage, .successMessage');
        message.hide();
    });

    if($('.edit-icon').length){
        $('.edit-icon').on('click', function (){
            $('.edit-news').removeClass('hidden');
            const title = $(this).data('title');
            const content = $(this).data('content');
            const id = $(this).data('id');

            $('[name ="edit-title"]', $('.edit-news-content')).val(title);
            $('[name ="edit-content"]', $('.edit-news-content')).val(content);
            $('[name ="news-id"]', $('.edit-news-content')).val(id);
        })
    }
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

async function readAllMessage() {

    const response = await fetch(`/private-message/read-all`, {
            method: "POST"
        }
    );

    if (response.status === 204) {
        const messages = document.querySelectorAll('.message-unread')
        messages.forEach((message) => {
            message.classList.remove('message-unread');
            message.classList.add('message-read');
        })
    }
    document.querySelector('.icon-messages').classList.remove('fa-bounce');
}

function updateCountdown() {
    const countdownElements = document.querySelectorAll("[id^='countdown-']");

    countdownElements.forEach(countdownElement => {
        const targetDate = new Date(countdownElement.getAttribute("data-target-date")).getTime();

        function update() {
            const currentDate = new Date().getTime();
            const timeLeft = targetDate - currentDate;

            if (timeLeft <= 0) {
                countdownElement.innerHTML = "00:00:00";
            } else {
                const hours = String(Math.floor((timeLeft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))).padStart(2, '0');
                const minutes = String(Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60))).padStart(2, '0');
                const seconds = String(Math.floor((timeLeft % (1000 * 60)) / 1000)).padStart(2, '0');

                countdownElement.innerHTML = `${hours}:${minutes}:${seconds}`;
            }
        }

        update();
        setInterval(update, 1000);
    });
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

function sellEggConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        var confirmation = confirm("Czy na pewno chcesz sprzedać to jajko?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function buyEggConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz kupić to jajko?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function undoEggConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz wycofać to jajko ze sprzedaży?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function sellItemConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        var confirmation = confirm("Czy na pewno chcesz sprzedać ten przedmiot?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function buyItemConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz kupić ten przedmiot?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function undoItemConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        const confirmation = confirm("Czy na pewno chcesz wycofać ten przedmiot ze sprzedaży?");
        if (!confirmation) {
            event.preventDefault();
        }
    }
}

function openForm(id) {
    document.getElementById("myForm-" + id).style.display = "block";
}

function openForm2(id) {
    document.getElementById("myForm2-" + id).style.display = "block";
}

function closeForm(id) {
    document.getElementById("myForm-" + id).style.display = "none";
}

function closeForm2(id) {
    document.getElementById("myForm2-" + id).style.display = "none";
}



