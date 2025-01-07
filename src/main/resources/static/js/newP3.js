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

    fetch("http://localhost:5008/P3SongQuiz")
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

async function startQuiz(currentSong) {
    console.log("vi är i start quiz!!")

    const quizDataXML = `
            <quizData>
            <title>${currentSong.title}</title>
            <title>${currentSong.artist}</title>
            </quizData>`;


    try {
        const response = await fetch("http://localhost:5008/startQuiz", {
            method: "POST",
            headers: {
                "Content-Type" : "application/xml"
            },
            body: quizDataXML
        });

        const responseText = await response.text();
        console.log(responseText);
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(responseText, "application/xml");

        if(!response.ok) {
            throw new Error("Något gick fel... " + response.status);
        }

        const rootNode = xmlDoc.querySelector("sr");
        if(!rootNode){
            throw new Error("Root node sr not found");
        }

        const playlist = xmlDoc.querySelector("playlist");
        if(!playlist){
            throw new Error("JSONObject [playlist] not found")
        }

        console.log("mottagen data: " + xmlDoc);
    } catch (error) {
        console.error("Det gick inte att skicka förfrågan:", error);
    }
}