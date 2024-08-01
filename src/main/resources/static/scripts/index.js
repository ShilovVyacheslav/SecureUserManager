var totalPages = parseInt(document.getElementById("total_pages").textContent);
let pageInputElem = document.getElementById("page_input");
let pageNumberElem = document.getElementById("page_number");
pageNumberElem.textContent = (Number(pageNumberElem.textContent) - (-1)).toString();

document.querySelector('.sender').addEventListener('click', function() {
    pageInputElem.value = 0;
    document.querySelector(".request_handler").submit();
});

document.getElementById("slide_back").addEventListener('click', function () {
    if (pageInputElem.value > 0) {
        pageInputElem.value -= 1;
        document.querySelector(".request_handler").submit();
    }
});

document.getElementById("slide_next").addEventListener('click', function () {
    if (pageInputElem.value < totalPages - 1) {
        pageInputElem.value -= -1;
        document.querySelector(".request_handler").submit();
    }
});
