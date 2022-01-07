package dk.simonsejse.discordbot.interfaces;

import dk.simonsejse.discordbot.exceptions.ResponseFetchException;
import okhttp3.Headers;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface IHttpManager {
    String get(URI uri, Headers headers) throws IOException, ResponseFetchException;
    String post(URI uri, Headers headers, RequestBody body) throws IOException, URISyntaxException, ResponseFetchException;
}
