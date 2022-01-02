package dk.simonsejse.discordbot.interfaces;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface IHttpManager {
    Response get(URI uri, Headers headers) throws IOException;
    Response post(URI uri, Headers headers, RequestBody body) throws IOException, URISyntaxException;
}
