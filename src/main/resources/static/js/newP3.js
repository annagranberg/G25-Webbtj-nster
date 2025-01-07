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

// När man vill spela quiz klickas denna knapp
document.getElementById("play-quiz").addEventListener("click", function () {
    console.log("Play quiz knappen klickades");

    fetch("http://localhost:5008/P3SongQuiz") // vi hämtar vår egna endpoint här, där vi får svar från SRservice
        .then(response => {
            if (!response.ok) {
                throw new Error("Något gick fel med förfrågan. Statuskod: " + response.status);
            }
            return response.text();
        })
        .then(data => {
            console.log("Mottagen data:", data);
            startQuiz(data);
        })
        .catch(error => {
            console.error("Det gick inte att hämta data:", error);
        });
});

// ny metod för att hämta SR låtlista via vårt egna API
document.getElementById("no-Quiz").addEventListener("click", function () {
    console.log("Knappen klickades");
    playQuiz.style.display = "none";
    document.getElementById("quiz-container").style.display = "none";

    fetch("http://localhost:5008/P3PlayList")// vi hämtar vår egna endpoint här, där vi får svar från SRservice
        .then(response => response.text())
        .then(data => {
            console.log("Raw response: " + data);

            // Parsar XML-String
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(data, "application/xml"); // Skapa xmlDoc

            // Kontrollerar så att xmlDoc är korrekt
            if (xmlDoc) {
                displayPlaylist(xmlDoc); // Skicka xmlDoc till displayPlaylist för att visa på hemsidan
            } else {
                console.error("Kunde inte parsa XML.");
            }
        })
        .catch(error => {
            console.error("Det gick inte att hämta låtlista: " + error);
        });
});

// Metod för att visa låtarna som spelas ifall man inte vill spela quiz
function displayPlaylist(xmlDoc) {
    const playListContainer = document.getElementById("playList-container");
    playListContainer.innerHTML = ''; // Töm container

    console.log("Vi är i displayPlaylist");

    // Hämta den nuvarande låten från XML
    const currentSongElement = xmlDoc.getElementsByTagName("song")[0];
    if (currentSongElement) {
        const currentSongTitle = currentSongElement.getElementsByTagName("title")[0]?.textContent || "Okänd titel";
        const currentSongArtist = currentSongElement.getElementsByTagName("artist")[0]?.textContent || "Okänd artist";

        // Skapar HTML-element för att visa den aktuella låten
        const currentSongHTML = document.createElement("p");
        currentSongHTML.textContent = `Nuvarande låt: ${currentSongTitle} av ${currentSongArtist}`;
        playListContainer.appendChild(currentSongHTML);
    } else {
        const currentSongHtml = document.createElement("p");
        currentSongHtml.textContent = 'Det spelas ingen låt just nu';
        playListContainer.appendChild(currentSongHtml);
        console.error("Kunde inte hitta information om den aktuella låten.");
    }

    // Hämta den föregående låten från XML
    const previousSongElement = xmlDoc.getElementsByTagName("previoussong")[0];
    if (previousSongElement) {
        const previousSongTitle = previousSongElement.getElementsByTagName("title")[0]?.textContent || "Okänd titel";
        const previousSongArtist = previousSongElement.getElementsByTagName("artist")[0]?.textContent || "Okänd artist";

        // Skapa HTML-element för att visa den föregående låten
        const previousSongHTML = document.createElement("p");
        previousSongHTML.textContent = `Föregående låt: ${previousSongTitle} av ${previousSongArtist}`;
        playListContainer.appendChild(previousSongHTML);
    } else {
        console.error("Kunde inte hitta information om den föregående låten.");
    }
}

async function startQuiz(currentSong) {
    console.log("vi är i start quiz!!")

    const quizData = {
        title: currentSong.title,
        artist: currentSong.artist
    };

    try {
        const response = await fetch("http://localhost:5008/startQuiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/json" // Skicka data som JSON
            },
            body: JSON.stringify(quizData) // Konvertera data till JSON och skicka
        });

        if (response.ok) {
            const quizResult = await response.json();
            console.log("Quizresultat:", quizResult);

            // Gör något med resultatet från backend (t.ex. visa alternativ för quizet)
            displayQuizOptions(quizResult);
        } else {
            console.error("Fel vid att skicka data till backend:", response.status);
        }
    } catch (error) {
        console.error("Det gick inte att skicka förfrågan:", error);
    }
}

async function displayQuizOptions(quizResult){

}

document.getElementById("submit-answer").addEventListener("click", function(){
    submitAnswer.style.display = "none";

});