const apiURL =  "https://sverigesradio.se/topsy/direkt/srapi/207.mp3";
const audioElement = document.getElementById("P4-player");
const playButton = document.getElementById("play-button");
const spelLista = document.getElementById("playlist-button");

document.getElementById("playlist-button").addEventListener("click", function () {
    audioElement.src = apiURL;
    playButton.style.display = "none";
    audioElement.style.display = "block";
    audioElement.play()
        .then(() => console.log("Spelar ljudströmmen"))
        .catch(error => console.error("Fel vid uppspelning: ", error));
});

spelLista.addEventListener("click", () => {
    fetchPlaylist();
})

async function fetchPlaylist() {
    try {
        //hämta spellista från API:et
        const response = await fetch("https://api.sr.se/api/v2/playlists/rightnow?channelid=207");

        if (!response.ok) {
            throw new Error("HTTP-status: " + response.status);
        }
        //läsa svaret som text (xml-format)
        const textResponse = await response.text();

        //konvertera xml till JavaScript-objekt
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(textResponse, "application/xml");

        //hämta den senaste låten
        const previousSong = xmlDoc.getElementById("previousSong")[0];
        const previousSongTitle = previousSong ? previousSong.getElementsByTagName("title")[0].textContent : "";
        const previousSongArtist = previousSong ? previousSong.getElementsByTagName("artist")[0].textContent : "";

        //hämta den nuvarande låten
        const currentSong = xmlDoc.getElementsByTagName("song")[0];
        const currentSongTitle = currentSong ? currentSong.getElementsByTagName("title")[0].textContent : "";
        const currentSongArtist = currentSong ? currentSong.getElementsByTagName("artist")[0].textContent : "";

        const previousSongContainer = document.querySelector(".previous-song");
        const currentSongContainer = document.querySelector(".current-song");

        previousSongContainer.textContent = `Tidigare spelad: ${previousSongArtist} - ${previousSongArtist}`;
        currentSongContainer.textContent = `Nu spelas: ${currentSongTitle} - ${currentSongArtist}`;

        //hämta spellistan om du vill visa hela spellistan också
        const songs = xmlDoc.getElementsByTagName("song");
        const playlistContainer = document.querySelector(".playlist");

        playlistContainer.innerHTML = '';

        if (songs.length > 0) {
            const ul = document.createElement("ul");
            Array.from(songs).forEach(song => {
                const title = song.getElementsByTagName("title")[0].textContent;
                const artist = song.getElementsByTagName("artist")[0].textContent;

                const li = document.createElement("li");
                li.textContent = `${title} - ${artist}`;
                ul.appendChild(li);
            });
            playlistContainer.appendChild(ul);
        }
    } catch (error) {
        console.error('Fel vid hämtning av spellista', error);
    }
}