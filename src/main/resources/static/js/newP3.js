const apiUrl = "https://sverigesradio.se/topsy/direkt/srapi/164.mp3";
const audioElement = document.getElementById("P3-player");
const playButton = document.getElementById("play-button");
const playQuiz = document.getElementById("play-quiz");
const submitAnswer = document.getElementById("submit-answer");

document.getElementById("play-button").addEventListener("click", function() {
    audioElement.src = apiUrl;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement.play()
        .then(() => console.log("Spelar ljudströmmen"))
        .catch(error => console.error("Fel vid uppspelning:", error));
});

document.getElementById("play-quiz").addEventListener("click", function () {
    console.log("Play quiz knappen klickades");
    startQuiz();

});

// ny metod för att hämta SR låtlista via vårt egna API
document.getElementById("no-Quiz").addEventListener("click", function () {
    console.log("Knappen klickades");
    playQuiz.style.display = "none";
    document.getElementById("quiz-container").style.display = "none";

    fetch("http://localhost:5008/P3PlayList")
        .then(response => response.json())
        .then(data => {
            console.log("Raw response: " + data);
            // Kontrollerar så att xmlDoc är korrekt
            if (data && data.playlist) {
                displayPlaylist(data); // Skicka xmlDoc till displayPlaylist
            } else {
                console.error("Kunde inte parsa XML.");
            }
        })
        .catch(error => {
            console.error("Det gick inte att hämta låtlista: " + error);
        });
});

function displayPlaylist(data) {
    const playListContainer = document.getElementById("playList-container");
    playListContainer.innerHTML = ''; // Töm container

    console.log("Vi är i displayPlaylist");

    if (data && data.playlist) {
        const playlist = data.playlist;

        const previousSong = {
            artist: playlist.previoussong.artist || "Okänd artist",
            title: playlist.previoussong.title || "Okänd titel"
        }
        const currentSong = {
            artist: playlist.song.artist || "Okänd artist",
            title: playlist.song.title || "Okänd titel"
        }

        const currentSongHTML = document.createElement("p");
        currentSongHTML.textContent = `Nuvarande låt: ${currentSong.title} av ${currentSong.artist}`;
        playListContainer.appendChild(currentSongHTML);

        const previousSongHTML = document.createElement("p");
        previousSongHTML.textContent = `Föregående låt: ${previousSong.title} av ${previousSong.artist}`;
        playListContainer.appendChild(previousSongHTML);

    } else {
        const currentSongHtml = document.createElement("p");
        currentSongHtml.textContent = 'Det spelas ingen låt just nu';
        playListContainer.appendChild(currentSongHtml);
        console.error("Kunde inte hitta information om den aktuella låten.");
    }
}

async function startQuiz() {
    console.log("vi är i start quiz!!");
    playQuiz.style.display = "none";
    submitAnswer.style.display = "block";

    try {
        const response = await fetch("http://localhost:5008/startQuiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const responseText = await response.json();
        console.log("Mottagen API-data:", responseText);

        if (!responseText) {
            throw new Error("Något gick fel... Inget svar från servern.");
        }

        const quizQuestion = responseText.Question;
        const answers = responseText.Answers;

        document.getElementById("quiz-question").textContent = quizQuestion;

        const optionsContainer = document.getElementById("quiz-options");
        optionsContainer.innerHTML = "";

        answers.forEach((answer) => {
            let song = answer.TEXT;

            const optionHTML = `
                <label>
                    <input type="radio" name="quiz-option" value="${song}">
                        ${song}
                </label><br>
                `;
            optionsContainer.innerHTML += optionHTML;
        });

    } catch (error) {
        console.error("Det gick inte att skicka förfrågan:", error);
    }
}
