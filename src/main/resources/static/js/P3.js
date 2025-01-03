const apiUrl = "/api/P3";
const playlistApiUrl = "/api/P3/playlist";
const audioElement = document.getElementById("P3-player");
const playButton = document.getElementById("play-button");
const playQuiz = document.getElementById("play-quiz");
const submitAnswer = document.getElementById("submit-answer");
let currentSongTitle = "";
let currentSongArtist = "";

// Starta ljudströmmen
document.getElementById("play-button").addEventListener("click", function () {
    audioElement.src = apiUrl;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement
        .play()
        .then(() => console.log("Spelar ljudströmmen"))
        .catch((error) => console.error("Fel vid uppspelning:", error));
});

// startar quiz
document.getElementById("play-quiz").addEventListener("click", function () {
    fetchCurrentSong();
    playQuiz.style.display = "none";
    submitAnswer.style.display = "block";
});

// Hämtar den aktuella låten
async function fetchCurrentSong() {
    try {
        const response = await fetch(playlistApiUrl);

        if (!response.ok) {
            throw new Error("HTTP-status: " + response.status);
        }

        const playlist = await response.json();
        currentSongTitle = playlist.currentSong.title;
        currentSongArtist = playlist.currentSong.artist;

        startQuiz();
    } catch (error) {
        console.error("Fel vid hämtning av spellista:", error);
    }
}

// Starta quizet
async function startQuiz() {
    const quizOptionsContainer = document.getElementById("quiz-options");
    const questionContainer = document.getElementById("quiz-question");
    const feedbackContainer = document.getElementById("quiz-feedback");

    quizOptionsContainer.innerHTML = "";
    feedbackContainer.textContent = "";

    const response = await fetch(`/api/P3/recommendations?title=${encodeURIComponent(currentSongTitle)}&artist=${encodeURIComponent(currentSongArtist)}`);

    if (!response.ok) {
        throw new Error("Kunde inte hämta rekommendationer");
    }

    const recommendations = await response.json();
    const allOptions = [...recommendations, { title: currentSongTitle, artist: currentSongArtist }];
    shuffleArray(allOptions);

    questionContainer.textContent = "Vilken låt spelas nu?";
    allOptions.forEach((option) => {
        const optionHTML = `
            <label>
                <input type="radio" name="quiz-option" value="${option.title} - ${option.artist}">
                ${option.title} - ${option.artist}
            </label><br>
            `;
        quizOptionsContainer.innerHTML += optionHTML;
    });
}

document.getElementById("submit-answer").addEventListener("click", function () {
    const selectedOption = document.querySelector('input[name="quiz-option"]:checked');
    const feedbackContainer = document.getElementById("quiz-feedback");

    if (!selectedOption) {
        feedbackContainer.textContent = "Välj ett alternativ innan du skickar in!";
        return;
    }

    const correctAnswer = `${currentSongTitle} - ${currentSongArtist}`;
    if (selectedOption.value === correctAnswer) {
        feedbackContainer.textContent = "Rätt svar! Bra jobbat!";
    } else {
        feedbackContainer.textContent = `Fel svar. Rätt svar är: ${correctAnswer}`;
    }
});

// Blanda alternativen
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}
