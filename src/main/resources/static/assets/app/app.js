document.addEventListener("DOMContentLoaded", () => {
    const context = $("meta[name=context]").attr('content');

    initAlerts();
    initCountdowns();
    initSelect2();
    initPopups();

    document.querySelector('.right-sidebar-toggle').addEventListener('click', function () {
        document.querySelector('.sidebar-right').classList.toggle('open');
    });

    document.getElementById('logout').addEventListener('click', function () {
        localStorage.removeItem('activeTab');
    });

    if (context === 'home') {
        $('.act-add-post').on('click', function () {
            if (!CKEDITOR.instances['content']) {
                CKEDITOR.replace('content');
            }
            CKEDITOR.instances['content'].setData("");
            $('.new-news-content').toggleClass("hidden");
        });

        $('.act-edit-post').on('click', function () {
            if (!CKEDITOR.instances['edit-content']) {
                CKEDITOR.replace('edit-content');
            }
            $('.edit-news').removeClass('hidden');
            const title = $(this).data('title');
            const content = $(this).data('content');
            const id = $(this).data('id');
            $('[name="edit-title"]', $('.edit-news')).val(title);
            $('[name="edit-content"]', $('.edit-news')).val(content);
            $('[name="news-id"]', $('.edit-news')).val(id);
            CKEDITOR.instances['edit-content'].setData(content);
            setTimeout(() => {
                window.scrollTo({
                    top: document.body.scrollHeight,
                    behavior: "smooth",
                });
            }, 250);
        });
    }

    if (context === 'private-messages') {

        $('.message-title').on('click', function () {
            $('.message-content', $(this).parent()).toggleClass('hidden');
        });

        $('.act-read-message').on('click', function () {
            const messageId = $(this).attr('data-message-id');
            fetch('/private-message/' + messageId + '/read', {
                method: "POST"
            }).then((response) => {
                if (response.status === 204) {
                    $(this).removeClass('message-unread');
                    $(this).addClass('message-read');
                }
                if ($('.message-unread').length === 0) {
                    $('.icon-messages').removeClass('fa-bounce');
                }
            });
        });

        $('.act-read-all').on('click', function () {
            fetch('/private-message/read-all', {
                method: "POST"
            }).then((response) => {
                if (response.status === 204) {
                    $('.message-unread').removeClass('message-unread').addClass('message-read');
                    $('.icon-messages').removeClass('fa-bounce');
                }
            });
        });

        $('.act-reply').on('click', function () {
            const sender = $(this).data('sender');
            const title = $(this).data('title');
            $('#create-header').trigger('click');
            $('[name="title"]', $('#create-message')).val("Re: " + title);
            const newOption = new Option(sender, sender, true, true);
            $('[name="recipient"]').append(newOption).trigger('change');
        });

        $('.act-delete').on('click', function () {
            if (!confirm("Czy na pewno chcesz usunąć tą wiadomość?")) {
                event.preventDefault();
            }
        });

        $('h2', $('.header-container')).on('click', function () {
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
            $('[name="recipient"]').append(new Option(recipient, recipient, true, true)).trigger('change');
        }
    }

    if (context === 'market') {

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

    if (context === 'guards' || context === 'arena') {
        const turtleName = $('#turtle-name').text();
        const opponent = $('#opponent-name').text();

        $('#battle-log').find('.card').each(function () {
            let colored = $(this).html();
            colored = colored.split(turtleName).join('<strong class="text-success">' + turtleName + '</strong>');
            colored = colored.split(opponent).join('<strong class="text-danger">' + opponent + '</strong>');
            $(this).html(colored);
        });

        $('#battle-log').find('.log').each(function (i) {
            setInterval(() => {
                $(this).fadeIn()
            }, i * 1000);
        });
        setTimeout(() => {
            $('.act-skip').hide();
            $('.act-back').show();
        }, $('#battle-log').find('.log').length * 1000 - 1000);
        $('.act-skip').on('click', function () {
            $('.act-skip').hide();
            $('.act-back').show();
            $('#battle-log').find('.log').fadeIn();
        });
    }

    if (context === 'academy') {


        $('[name="durationTime"]').change(function () {
            const $parent = $(this).closest('form');
            const trainingId = $('[name = "training"]', $parent).attr('data-trainingId');
            const duration = $(this).val();
            const requestData = {
                trainingId: trainingId,
                duration: duration

            }
            const $submitButton = $('input[type="submit"]', $parent);


            let multiplier = 1;
            if (duration === '180') {
                multiplier = 2;
            } else if (duration === '360') {
                multiplier = 3;
            }
            $.ajax({
                url: "/academy/if-training-can",
                data: requestData,
                type: "GET",
                success: function (response) {
                    if (response === true) {
                        $submitButton.prop('disabled', false);
                    } else {
                        $submitButton.prop('disabled', true);
                    }
                }
            })
            $('.training-item', $parent).each(function () {
                const cost = $(this).data('cost');

                $('.act-target-cost', $(this)).text(cost * multiplier);
            });
        });
    }

    if (context === 'friends') {
        $('[name="friendUsername"]').on('change', function () {
            if ($(this).val()) {
                $('.act-view-profile').removeAttr('disabled');
                $('.act-add-friend').removeAttr('disabled');
            }
        });
        $('.act-view-profile').on('click', function () {
            if ($('[name="friendUsername"]').val()) {
                const $form = $(this).closest('form');
                $form.attr('action', '/user/profile');
                $form.attr('method', 'POST');
                $form.submit();
            }
        });
        $('.act-add-friend').on('click', function () {
            if ($('[name="friendUsername"]').val()) {
                const $form = $(this).closest('form');
                $form.attr('action', '/friends/add');
                $form.attr('method', 'POST');
                $form.submit();
            }
        });
    }
});

function initCountdowns() {
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

function initAlerts() {
    $('.closeButton').on('click', function () {
        const message = $(this).closest('.errorMessage, .successMessage');
        message.hide();
    });
}

function initSelect2() {
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
}

function initPopups() {
    $('.popup-backdrop').on('click', function (e) {
        if (e.target.classList.contains('popup-backdrop')) {
            $(this).fadeOut();
        }
    });
}

function abandonTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz porzucić tego żółwia?")) {
            event.preventDefault();
        }
    }
}

function sellTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz sprzedać tego żółwia?")) {
            event.preventDefault();
        }
    }
}

function buyTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz kupić tego żółwia?")) {
            event.preventDefault();
        }
    }
}

function undoTurtleConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz wycofać tego żółwia ze sprzedaży?")) {
            event.preventDefault();
        }
    }
}

function adoptEggConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz zaadoptować to jajko?")) {
            event.preventDefault();
        }
    }
}

function sellEggConfirm(buttonElement) {
    var form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz sprzedać to jajko?")) {
            event.preventDefault();
        }
    }
}

function buyEggConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz kupić to jajko?")) {
            event.preventDefault();
        }
    }
}

function undoEggConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz wycofać to jajko ze sprzedaży?")) {
            event.preventDefault();
        }
    }
}

function buyItemConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz kupić ten przedmiot?")) {
            event.preventDefault();
        }
    }
}

function undoItemConfirm(buttonElement) {
    const form = buttonElement.closest("form");
    if (form) {
        if (!confirm("Czy na pewno chcesz wycofać ten przedmiot ze sprzedaży?")) {
            event.preventDefault();
        }
    }
}

function openPopupSellItem(id) {
    const $popup = $('#popup-sell-item');
    const route = '/items/' + id + '/details';
    $popup.find('form').attr('action', route);
    $popup.fadeIn();
}

function openPopupSellEgg(id) {
    const $popup = $('#popup-sell-egg');
    const route = '/nest/' + id + '/sell';
    $popup.find('form').attr('action', route);
    $popup.fadeIn();
}

function openPopupSellTurtle(id) {
    const $popup = $('#popup-sell-turtle');
    const route = '/turtles/' + id + '/sell';
    $popup.find('form').attr('action', route);
    $popup.fadeIn();
}

function openPopupAdoptEgg(id) {
    const $popup = $('#popup-adopt-egg');
    const route = '/nest/' + id + '/adopt';
    $popup.find('form').attr('action', route);
    $popup.fadeIn();
}

function openPopupMoreInfo(trigger) {
    const $popup = $('#popup-more-info');
    const $items = $(trigger).closest('.expedition-card').find('.expedition-items').clone();
    const $content = $('.popup-content', $popup);
    $content.html('');
    $items.appendTo($content);
    $items.show();
    $popup.fadeIn();
}