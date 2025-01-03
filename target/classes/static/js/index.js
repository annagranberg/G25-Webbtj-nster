
document.addEventListener("DOMContentLoaded", function (){
    fetch('/html/header.html')
        .then(response => response.text())
        .then(html => {
            document.querySelector('body').insertAdjacentHTML('afterbegin', html);
        });
});