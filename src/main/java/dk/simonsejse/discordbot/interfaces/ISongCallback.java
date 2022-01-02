package dk.simonsejse.discordbot.interfaces;

import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;

public interface ISongCallback {
    void success(SpotifyTrackInfoData spotifyTrackInfo);
    void failure(int code, String errorMsg, boolean isTokenExpired);
}
