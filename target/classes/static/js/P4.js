const apiURL =  "https://sverigesradio.se/topsy/direkt/srapi/207.mp3";
const audioElement = document.getElementById("P4-player");
const playButton = document.getElementById("play-button");
const playQuiz = document.getElementById("play-quiz");
const submitAnswer = document.getElementById("submit-answer");
const nextQuestion = document.getElementById("next-question");
const feedback = document.getElementById("quiz-feedback");
let currentSong = null;

document.getElementById("play-button").addEventListener("click", function() {
    audioElement.src= apiURL;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement.play()
        .then(() => console.log("Playing the audio stream"))
        .catch(error => console.error("Playback error:", error));
});


document.getElementById("play-quiz").addEventListener("click", function () {
    startQuiz();
});

// ny metod för att hämta SR låtlista via vårt egna API
document.getElementById("no-Quiz").addEventListener("click", function () {
    playQuiz.style.display = "none";
    document.getElementById("quiz-container").style.display = "none";

    fetch("http://localhost:5008/channels/4/playlist")
        .then(response => response.json())
        .then(data => {
            console.log("api data received:" + data); //testar debug
            console.log("Raw response: " + data);
            if (data && data.playlist) {
                displayPlaylist(data);
            } else {
                console.error("Could not parse XML.");
            }
        })
        .catch(error => {
            console.error("Unable to retrieve playlist: " + error);
        });
});

function displayPlaylist(data) {
    const playListContainer = document.getElementById("playList-container");
    playListContainer.innerHTML = ''; // Töm container

    if (data && data.playlist) {
        const playlist = data.playlist;

        // Hantera föregående låt
        const previousSong = playlist.previoussong
            ? {
                artist: playlist.previoussong.artist || "Unknown artist",
                title: playlist.previoussong.title || "Unknown title"
            }
            : { artist: "Unknown artist", title: "Unknown title" };

        // Visa föregående låt
        const previousSongHTML = document.createElement("p");
        previousSongHTML.textContent = `Previous song: ${previousSong.title} av ${previousSong.artist}`;
        playListContainer.appendChild(previousSongHTML);

        // Hantera aktuell låt
        if (playlist.song) {
            const currentSong = {
                artist: playlist.song.artist || "Unknown artist",
                title: playlist.song.title || "Unknown title"
            };

            // Visa aktuell låt
            const currentSongHTML = document.createElement("p");
            currentSongHTML.textContent = `Current song: ${currentSong.title} av ${currentSong.artist}`;
            playListContainer.appendChild(currentSongHTML);
        } else {
            // Om det inte finns någon aktuell låt
            const noCurrentSongHTML = document.createElement("p");
            noCurrentSongHTML.textContent = "There is no song playing right now.";
            playListContainer.appendChild(noCurrentSongHTML);
        }
    } else {
        // Om `playlist` inte hittas
        const noPlaylistHTML = document.createElement("p");
        noPlaylistHTML.textContent = "The playlist could not be retrieved.";
        playListContainer.appendChild(noPlaylistHTML);
        console.error("Playlist is missing from the API response.");
    }
}

async function startQuiz() {
    playQuiz.style.display = "none";
    submitAnswer.style.display = "block";

    try {
        const response = await fetch("http://localhost:5008/channels/4/quiz", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const responseText = await response.json();
        console.log("Mottagen API-data:", responseText);

        if (!responseText.Answers || responseText.Answers.length === 0) {
            throw new Error("No response options were received. Probably because no song is playing.");
        }

        const quizQuestion = responseText.Question;
        let answers = responseText.Answers;

        const uniqueAnswer = [];
        const seen = new Set();
        answers.forEach(answer => {
            const key = answer.TEXT.toLowerCase();
            if (!seen.has(key)) {
                seen.add(key);
                uniqueAnswer.push(answer);
            }
        });

        console.log("Unika svar:", uniqueAnswer);

        //lägger till hårdkodade alternativ när det finns färre än 2 unika svar
        if (uniqueAnswer.length < 2) {
            console.warn("Not enough unique answers, adding hardcoded options");

            const hardcodedAnswers = [
                {TEXT: "Ida summer song", CORRECT: false},
                {TEXT: "Pippi Longstocking", CORRECT: false},
                {TEXT: "Bä bä vita lamm", CORRECT: false}
            ];

            //lägg till hårdkodade alternativ tills vi har minst 3 alternativ
            while (uniqueAnswer.length < 3 && hardcodedAnswers.length > 0) {
                uniqueAnswer.push(hardcodedAnswers.shift());
            }
        }

        const limitedAnswers = shuffleArray(uniqueAnswer).slice(0, 5);

        //blanda alternativen
        const correctAnswer = limitedAnswers.find(answer => answer.CORRECT === true);

        document.getElementById("quiz-question").textContent = quizQuestion;

        const optionsContainer = document.getElementById("quiz-options");
        optionsContainer.innerHTML = "";

        limitedAnswers.forEach(answer => {
            const optionHTML = `
                <label>
                    <input type="radio" name="quiz-option" value="${answer.TEXT}">
                    ${answer.TEXT}
                </label><br>
            `;
            optionsContainer.innerHTML += optionHTML;
        });

        document.getElementById("submit-answer").onclick = function () {
            const selectedOption = document.querySelector('input[name="quiz-option"]:checked');
            feedback.innerHTML = "";

            if (!selectedOption) {
                feedback.innerHTML = "You must choose an option!";
                return;
            }

            const selectedAnswer = selectedOption.value;
            console.log("Selected answer:", selectedAnswer);

            if (selectedAnswer === correctAnswer.TEXT) {
                feedback.innerHTML = "Right answer! 🎉";
            } else {
                feedback.innerHTML = "Wrong answer ☹️";
            }

            submitAnswer.style.display = "none";
            nextQuestion.style.display = "block";
        };

    } catch (error) {
        console.error("Unable to start quiz:", error);
        feedback.innerHTML = "It is not possible to play the quiz right now, try again when a song is playing!";
        playQuiz.style.display = "block";
        submitAnswer.style.display = "none";
    }
}

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

document.getElementById("next-question").addEventListener("click", function () {
    console.log("Next question-button pressed");

    fetch("http://localhost:5008/P4PlayList")
        .then(response => response.json())
        .then(data => {
            const newSong = data.playlist?.song?.title || null;

            if (!newSong) {
                // Ingen låt spelas
                feedback.innerHTML = "No song is playing right now, sit back and wait for the next song to play the quiz again soon.";
                submitAnswer.style.display = "none";
                nextQuestion.style.display = "block";
                return;
            }

            if (newSong === currentSong) {
                // Samma låt spelas
                feedback.innerHTML = "You can't play the quiz until the next song.";
                submitAnswer.style.display = "none";
                nextQuestion.style.display = "block";
                return;
            }

            // Ny låt spelas
            currentSong = newSong;
            feedback.innerHTML = ""; // Rensa tidigare feedback
            console.log("New song found:", currentSong);
            startQuiz(); // Starta quiz med den nya låten
        })
        .catch(error => {
            console.error("Error downloading song:", error);
            feedback.innerHTML = "The current song could not be retrieved. Please try again later.";
        });
});
