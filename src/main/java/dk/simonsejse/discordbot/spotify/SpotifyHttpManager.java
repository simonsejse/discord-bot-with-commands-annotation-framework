package dk.simonsejse.discordbot.spotify;

import dk.simonsejse.discordbot.exceptions.ResponseFetchException;
import dk.simonsejse.discordbot.interfaces.IHttpManager;
import okhttp3.*;
import org.apache.commons.collections4.KeyValue;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

@Component
public class SpotifyHttpManager implements IHttpManager {
    private final OkHttpClient client;

    public SpotifyHttpManager() {
        this.client = new OkHttpClient();
    }

    @Override
    public String get(URI uri, Headers headers) throws IOException, ResponseFetchException {
        Request request = new Request.Builder()
                .url(uri.toURL())
                .headers(headers)
                .build();

        Call call = client.newCall(request);

        try(Response execute = call.execute()){
            if (execute.body() == null) throw new ResponseFetchException("Body could not be fetched modtaget status kode " + execute.code());
            if (execute.code() != HttpStatus.SC_OK) throw new ResponseFetchException("Modtaget respons-kode var ikke OK (200), modtaget statuskode er: " + execute.code());
            return execute.body().string();
        }
    }

    @Override
    public String post(URI uri, Headers headers, RequestBody body) throws IOException, ResponseFetchException {
        Request request = new Request.Builder()
                .url(uri.toURL())
                .headers(headers)
                .post(body)
                .build();
        Call call = client.newCall(request);

        try(final Response execute = call.execute()){
            if (execute.body() == null) throw new ResponseFetchException("Body could not be fetched modtaget status kode " + execute.code());
            if (execute.code() != HttpStatus.SC_OK) throw new ResponseFetchException("Modtaget respons-kode var ikke OK (200), modtaget statuskode er: " + execute.code() + " samt respons body er "+execute.body().string());
            return execute.body().string();
        }
    }


    @SafeVarargs
    public final Headers createHeaders(KeyValue<String, String>... keyValuePairs) {
        final Headers.Builder builder = new Headers.Builder();
        Arrays.stream(keyValuePairs).forEach(keyValuePair -> builder.add(keyValuePair.getKey(), keyValuePair.getValue()));
        return builder.build();
    }
}
