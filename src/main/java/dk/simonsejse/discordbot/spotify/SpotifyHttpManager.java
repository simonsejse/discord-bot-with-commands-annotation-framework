package dk.simonsejse.discordbot.spotify;

import dk.simonsejse.discordbot.interfaces.IHttpManager;
import okhttp3.*;
import org.apache.commons.collections4.KeyValue;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

@Component
public class SpotifyHttpManager implements IHttpManager {
    private final OkHttpClient client;

    public SpotifyHttpManager() {
        this.client = new OkHttpClient();
    }

    @Override
    public Response get(URI uri, Headers headers) throws IOException {

        Request request = new Request.Builder()
                .url(uri.toURL())
                .headers(headers)
                .build();

        final Call call = client.newCall(request);
        return call.execute();
    }

    @Override
    public Response post(URI uri, Headers headers, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(uri.toURL())
                .headers(headers)
                .post(body)
                .build();

        final Call call = client.newCall(request);
        return call.execute();
    }


    @SafeVarargs
    public final Headers createHeaders(KeyValue<String, String>... keyValuePairs) {
        final Headers.Builder builder = new Headers.Builder();
        Arrays.stream(keyValuePairs).forEach(keyValuePair -> builder.add(keyValuePair.getKey(), keyValuePair.getValue()));
        return builder.build();
    }
}
