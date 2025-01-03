
document.addEventListener("DOMContentLoaded", function (){
    fetch('/static/html/header.html')
        .then(response => response.text())
        .then(html => {
            document.querySelector('body').insertAdjacentElement('afterbegin', html);
        });
}