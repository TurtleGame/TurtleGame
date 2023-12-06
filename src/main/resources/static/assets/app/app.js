document.addEventListener("DOMContentLoaded", () => {
    const context = $("meta[name=context]").attr('content');


    if (['home'].includes(context)) {
        if ($('.new-news-button').length) {
            $(".new-news-button").on('click', function() {
                $(".new-news-content").toggleClass("hidden")
            });
            CKEDITOR.replace('content');
        }

        $('.edit-icon').on('click', function (){
            $('.edit-news').removeClass('hidden');
            const title = $(this).data('title');
            const content = $(this).data('content');
            const id = $(this).data('id');

            $('[name="edit-title"]', $('.edit-news-content')).val(title);
            $('[name="edit-content"]', $('.edit-news-content')).val(content);
            $('[name="news-id"]', $('.edit-news-content')).val(id);
        })
    }


    if (['academy', 'expeditions'].includes(context)) {
        updateCountdown();
    }


    if (['private-messages'].includes(context)) {
        document.querySelectorAll('.message-title').forEach((el) => {
            el.addEventListener('click', () => {
                el.closest('.message-container').querySelector(".message-content").classList.toggle('hidden');
            });
        });

        $('h2', $('.header-container')).on('click', function() {
            $('h2', $('.header-container')).removeClass('active-header');
            $(this).addClass('active-header');
            $('#received-message').css('display', 'none');
            $('#sent-message').css('display', 'none');
            $('#create-message').css('display', 'none');

            if ($(this).attr('id') === 'received-header') {
                $('#received-message').css('display', 'block');
            }
            if ($(this).attr('id') === 'sent-header') {
                $('#sent-message').css('display', 'block');
            }
            if ($(this).attr('id') === 'create-header') {
                $('#create-message').css('display', 'block');
            }
        });

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


    if (['market'].includes(context)) {
        function setActiveState() {
            const activeTab = localStorage.getItem('activeTab') || 'turtles';
            if (activeTab === 'turtles') {
                $('#turtles-selling').css('display', 'block');
                $('#items-selling').css('display', 'none');
                $('#turtles-header').addClass('active-header');
                $('#items-header').removeClass('active-header');
            } else {
                $('#turtles-selling').css('display', 'none');
                $('#items-selling').css('display', 'block');
                $('#items-header').addClass('active-header');
                $('#turtles-header').removeClass('active-header');
            }
        }

        function resetActiveState() {
            localStorage.removeItem('activeTab');

            $('#turtles-selling').css('display', 'block');
            $('#items-selling').css('display', 'none');
            $('#turtles-header').addClass('active-header');
            $('#items-header').removeClass('active-header');
        }

        setActiveState();

        $('#turtles-header').on('click', function () {
            $('#turtles-selling').css('display', 'block');
            $('#items-selling').css('display', 'none');
            $('#turtles-header').addClass('active-header');
            $('#items-header').removeClass('active-header');

            localStorage.setItem('activeTab', 'turtles');
        });

        $('#items-header').on('click', function () {
            $('#turtles-selling').css('display', 'none');
            $('#items-selling').css('display', 'block');
            $('#items-header').addClass('active-header');
            $('#turtles-header').removeClass('active-header');

            localStorage.setItem('activeTab', 'items');
        });

    }

    document.getElementById('logout').addEventListener('click', function () {
        resetActiveState();
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

    $('.closeButton').on('click', function () {
        const message = $(this).closest('.errorMessage, .successMessage');
        message.hide();
    });
});

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
    });
    if (response.status === 204) {
        $('.message-unread').removeClass('message-unread').addClass('message-read');
        $('.icon-messages').removeClass('fa-bounce');
    }
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
