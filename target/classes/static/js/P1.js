const apiURL =  "https://sverigesradio.se/topsy/direkt/srapi/132.mp3";
const playlistApiUrl = "/api/playlist";
const audioElement = document.getElementById("P1-player");
const playButton = document.getElementById("play-button");
const spelLista = document.getElementById("playlist-button");

document.getElementById("play-button").addEventListener("click", function () {
    audioElement.src = apiURL;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement
        .play()
        .then(() => console.log("spelar ljudströmmen"))
        .catch((error) => console.error("Fel vid uppspelning:", error));

});

// Hämta spellista
spelLista.addEventListener("click", () => {
    fetchPlaylist();
});

async function fetchPlaylist() {
    try {
        // Hämta spellista från servern
        const response = await fetch(playlistApiUrl);

        if (!response.ok) {
            throw new Error("HTTP-status: " + response.status);
        }

        // Läser svaret som JSON (vilket är anpassat till javalin)
        const playlist = await response.json();

        const previousSongContainer = document.querySelector(".previous-song");
        const currentSongContainer = document.querySelector(".current-song");
        const playlistContainer = document.querySelector(".playlist");

        // Uppdaterar tidigare och nuvarande låt
        const { previousSong, currentSong, songs } = playlist;

        previousSongContainer.textContent = `Tidigare spelad: ${previousSong.title} - ${previousSong.artist}`;
        currentSongContainer.textContent = `Nu spelas: ${currentSong.title} - ${currentSong.artist}`;

        playlistContainer.innerHTML = "";
        if (songs && songs.length > 0) {
            const ul = document.createElement("ul");
            songs.forEach((song) => {
                const li = document.createElement("li");
                li.textContent = `${song.title} - ${song.artist}`;
                ul.appendChild(li);
            });
            playlistContainer.appendChild(ul);
        }
    } catch (error) {
        console.error("Fel vid hämtning av spellista:", error);
    }
}