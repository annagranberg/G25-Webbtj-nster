const apiUrl = "https://sverigesradio.se/topsy/direkt/srapi/163.mp3";
const audioElement = document.getElementById("P2-player");
const playButton = document.getElementById("play-button");
const playQuiz = document.getElementById("play-quiz");
const submitAnswer = document.getElementById("submit-answer");
const nextQuestion = document.getElementById("next-question");
const feedback = document.getElementById("quiz-feedback");
let currentSong = null;

document.getElementById("play-button").addEventListener("click", function() {
    audioElement.src = apiUrl;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement.play()
        .then(() => console.log("Plays the audio stream"))
        .catch(error => console.error("Error while playing:", error));
});


document.getElementById("play-quiz").addEventListener("click", function () {
    console.log("The play quiz button was clicked");
    startQuiz();
});

// ny metod för att hämta SR låtlista via vårt egna API
document.getElementById("no-Quiz").addEventListener("click", function () {
    console.log("The button was clicked");
    playQuiz.style.display = "none";
    document.getElementById("quiz-container").style.display = "none";

    let channelId = 2; // Exempel på channelId
    fetch("http://localhost:5008/channels/2/playlist")
        .then(response => response.json())
        .then(data => {
            console.log("api-data received:" + data); //testar debug
            console.log("Raw response: " + data);
            // Kontrollerar så att xmlDoc är korrekt
            if (data && data.playlist) {
                displayPlaylist(data); // Skicka xmlDoc till displayPlaylist
            } else {
                console.error("Failed to parse XML.");
            }
        })
        .catch(error => {
            console.error("Unable to load playlist: " + error);
            const playListContainer = document.getElementById("playList-container");
            playListContainer.innerHTML = "There is no song playing right now"; // Töm container
        });
});

function displayPlaylist(data) {
    const playListContainer = document.getElementById("playList-container");
    playListContainer.innerHTML = ''; // Töm container

    console.log("We are in displayPlaylist");

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
                artist: playlist.song.artist || "Okänd artist",
                title: playlist.song.title || "Okänd titel"
            };

            // Visa aktuell låt
            const currentSongHTML = document.createElement("p");
            currentSongHTML.textContent = `Current song: ${currentSong.title} from ${currentSong.artist}`;
            playListContainer.appendChild(currentSongHTML);
        } else {
            // Om det inte finns någon aktuell låt
            const noCurrentSongHTML = document.createElement("p");
            noCurrentSongHTML.textContent = "There is no song playing at the moment";
            playListContainer.appendChild(noCurrentSongHTML);
        }
    } else {
        // Om `playlist` inte hittas
        const noPlaylistHTML = document.createElement("p");
        noPlaylistHTML.textContent = "Playlist not found.";
        playListContainer.appendChild(noPlaylistHTML);
        console.error("Playlist missing in the API-answer.");
    }
}

async function startQuiz() {
    console.log("We are in startQuiz!");
    playQuiz.style.display = "none";
    submitAnswer.style.display = "block";

    try {
        const response = await fetch("http://localhost:5008/channels/2/quiz", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const responseText = await response.json();
        console.log("Received API-data:", responseText);

        if (!responseText.Answers || responseText.Answers.length === 0) {
            throw new Error("No response options received. Probably because there is no song playing at the moment.");
        }

        const quizQuestion = responseText.Question;
        let answers = responseText.Answers;
        //const answers = responseText.Answers;

        const uniqueAnswer = [];
        const seen = new Set();
        answers.forEach(answer => {
            const key = answer.TEXT.toLowerCase();
            if (!seen.has(key)) {
                seen.add(key);
                uniqueAnswer.push(answer);
            }
        });

        console.log("Unique svar:", uniqueAnswer);

        if (uniqueAnswer.length < 2) {

            console.warn("Not enough unique answers, adding hardcoded options");

            const hardcodedAnswers = [
                { TEXT: "Ida summer song", CORRECT: false},
                { TEXT: "Pippi Longstocking", CORRECT: false},
                { TEXT: "Bä bä vita lamm", CORRECT: false }
            ];

            while (uniqueAnswer.length < 3 && hardcodedAnswers.length > 0) {
                uniqueAnswer.push(hardcodedAnswers.shift());
            }
        }

        const limitedAnswers = shuffleArray(uniqueAnswer).slice(0, 5);
        console.log("Limited and mixed responses:", limitedAnswers);

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
                feedback.innerHTML = "You must select an potion!";
                return;
            }

            const selectedAnswer = selectedOption.value;
            console.log("Selected answer:", selectedAnswer);

            if (selectedAnswer === correctAnswer.TEXT) {
                feedback.innerHTML = "Correct Answer! 🎉";
            } else {
                feedback.innerHTML = "Wrong Answer ☹️";
            }

            submitAnswer.style.display = "none";
            nextQuestion.style.display = "block";
        };

    } catch (error) {
        console.error("Failed to start quiz:", error);
        feedback.innerHTML = "It is not possible to play quiz right now, Try again later!";
        playQuiz.style.display = "block";
        submitAnswer.style.display = "none";
    }
}

function shuffleArray(array) {
    console.log("We are in shuffleArray:");
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

document.getElementById("next-question").addEventListener("click", function () {
    console.log("Next question-knappen klickades");

    fetch("http://localhost:5008/P2PlayList")
        .then(response => response.json())
        .then(data => {
            const newSong = data.playlist?.song?.title || null;

            if (!newSong) {
                // Ingen låt spelas
                feedback.innerHTML = "No song currently playing, sit back and relax for the next song to play the quiz soon.";
                submitAnswer.style.display = "none";
                nextQuestion.style.display = "block";
                return;
            }

            if (newSong === currentSong) {
                // Samma låt spelas
                feedback.innerHTML = "You cannot play the song until the next song.";
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
            console.error("Error loading song:", error);
            feedback.innerHTML = "The current song could not be loaded. Try again later!";
        });
});


