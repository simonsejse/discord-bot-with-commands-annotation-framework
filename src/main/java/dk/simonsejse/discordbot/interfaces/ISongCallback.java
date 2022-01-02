package dk.simonsejse.discordbot.interfaces;

public interface ISongCallback {
    void success(String name);
    void failure(int code, String errorMsg, boolean isTokenExpired);
}
