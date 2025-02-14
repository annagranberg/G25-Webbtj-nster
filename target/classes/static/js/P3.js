const apiUrl = "https://sverigesradio.se/topsy/direkt/srapi/164.mp3";
const audioElement = document.getElementById("P3-player");
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
            console.log("api-data mottagen:" + data); //testar debug
            console.log("Raw response: " + data);

            if (data && data.playlist) {
                displayPlaylist(data); 
            } else {
                console.error("Kunde inte parsa XML.");
            }
        })
        .catch(error => {
            console.error("Det gick inte att hämta låtlista: " + error);
            const playListContainer = document.getElementById("playList-container");
            playListContainer.innerHTML = 'Det spelas ingen låt just nu'; // Töm container
        });
});

function displayPlaylist(data) {
    const playListContainer = document.getElementById("playList-container");
    playListContainer.innerHTML = ''; // Töm container

    console.log("Vi är i displayPlaylist");

    if (data && data.playlist) {
        const playlist = data.playlist;

        // Hantera föregående låt
        const previousSong = playlist.previoussong
            ? {
                artist: playlist.previoussong.artist || "Okänd artist",
                title: playlist.previoussong.title || "Okänd titel"
            }
            : { artist: "Okänd artist", title: "Okänd titel" };

        // Visa föregående låt
        const previousSongHTML = document.createElement("p");
        previousSongHTML.textContent = `Föregående låt: ${previousSong.title} av ${previousSong.artist}`;
        playListContainer.appendChild(previousSongHTML);

        // Hantera aktuell låt
        if (playlist.song) {
            const currentSong = {
                artist: playlist.song.artist || "Okänd artist",
                title: playlist.song.title || "Okänd titel"
            };

            // Visa aktuell låt
            const currentSongHTML = document.createElement("p");
            currentSongHTML.textContent = `Nuvarande låt: ${currentSong.title} av ${currentSong.artist}`;
            playListContainer.appendChild(currentSongHTML);
        } else {
            // Om det inte finns någon aktuell låt
            const noCurrentSongHTML = document.createElement("p");
            noCurrentSongHTML.textContent = "Det spelas ingen låt just nu.";
            playListContainer.appendChild(noCurrentSongHTML);
        }
    } else {
        // Om `playlist` inte hittas
        const noPlaylistHTML = document.createElement("p");
        noPlaylistHTML.textContent = "Låtlistan kunde inte hämtas.";
        playListContainer.appendChild(noPlaylistHTML);
        console.error("Playlist saknas i API-svaret.");
    }
}

async function startQuiz() {
    console.log("Vi är i startQuiz!");
    playQuiz.style.display = "none";
    submitAnswer.style.display = "block";

    try {
        const response = await fetch("http://localhost:5008/channels/3/quiz", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const responseText = await response.json();
        console.log("Mottagen API-data:", responseText);

        if (!responseText.Answers || responseText.Answers.length === 0) {
            throw new Error("Inga svarsalternativ mottogs. Förmodligen för att det inte spelas någon låt.");
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

        console.log("Unika svar:", uniqueAnswer);

        if (uniqueAnswer.length <= 2) {
            feedback.innerHTML = "Det gick inte att generera tillräckligt många unika svarsalternativ. Försök igen senare";
            playQuiz.style.display = "block";
            submitAnswer.style.display = "none";
            return;
        }

        const limitedAnswers = shuffleArray(uniqueAnswer).slice(0, 5);
        console.log("Begränsade och blandade svar:", limitedAnswers);

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
                feedback.innerHTML = "Du måste välja ett alternativ!";
                return;
            }

            const selectedAnswer = selectedOption.value;
            console.log("Valt svar:", selectedAnswer);

            if (selectedAnswer === correctAnswer.TEXT) {
                feedback.innerHTML = "Rätt svar! 🎉";
            } else {
                feedback.innerHTML = "Fel svar ☹️";
            }

            submitAnswer.style.display = "none";
            nextQuestion.style.display = "block";
        };

    } catch (error) {
        console.error("Det gick inte att starta quiz:", error);
        feedback.innerHTML = "Det går inte att spela quiz just nu, försök igen när en låt spelas!";
        playQuiz.style.display = "block";
        submitAnswer.style.display = "none";
    }
}

function shuffleArray(array) {
    console.log("Vi är i shuffleArray:");
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

document.getElementById("next-question").addEventListener("click", function () {
    console.log("Next question-knappen klickades");

    fetch("http://localhost:5008/P3PlayList")
        .then(response => response.json())
        .then(data => {
            const newSong = data.playlist?.song?.title || null;

            if (!newSong) {
                // Ingen låt spelas
                feedback.innerHTML = "Ingen låt spelas just nu, luta dig tillbaka och vänta på nästa låt för att spela quizet snart igen.";
                submitAnswer.style.display = "none";
                nextQuestion.style.display = "block";
                return;
            }

            if (newSong === currentSong) {
                // Samma låt spelas
                feedback.innerHTML = "Du kan inte spela quiz förrän nästa låt.";
                submitAnswer.style.display = "none";
                nextQuestion.style.display = "block";
                return;
            }

            // Ny låt spelas
            currentSong = newSong;
            feedback.innerHTML = ""; // Rensa tidigare feedback
            console.log("Ny låt hittad:", currentSong);
            startQuiz(); // Starta quiz med den nya låten
        })
        .catch(error => {
            console.error("Fel vid hämtning av låt:", error);
            feedback.innerHTML = "Det gick inte att hämta aktuell låt. Försök igen senare.";
        });
});
