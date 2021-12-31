package dk.simonsejse.discordbot.commands.weathercmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.models.weatherreq.WeatherResponse;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.temporal.ChronoUnit;

@Command(
        cmdName = "vejr",
        info = "Viser info om en vejret udfra bynavn!",
        cooldown = 1,
        unit = ChronoUnit.MINUTES,
        exclusions = {Role.OWNER},
        parameterNames = {"by_navn"},
        parameterDescriptions = {"Navnet på byen!"},
        types = {OptionType.STRING},
        isRequired = {true}
)
public class WeatherInfoCommand implements CommandPerform {

    private static final String BASE_URL = String.format("http://api.weatherapi.com/v1/current.json?key=%s&", System.getenv("WEATHER_API_TOKEN"));

    private final Messages messages;

    public WeatherInfoCommand(final Messages messages){
        this.messages = messages;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final String cityName = event.getOptions().get(0).getAsString();
        try {
            final URL url = new URL(BASE_URL + String.format("q=%s&aqi=no", cityName));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            final int responseCode = conn.getResponseCode();
            if (responseCode == 200){
                final InputStream inputStream = conn.getInputStream();
                final String data = new String(inputStream.readAllBytes());
                final ObjectMapper mapper = new ObjectMapper();

                final WeatherResponse response = mapper.readValue(data, WeatherResponse.class);
                final Message weatherReport = this.messages.getWeatherReport(response);
                event.deferReply(false).queue(iHook -> {
                    iHook.sendMessage(weatherReport).queue();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
