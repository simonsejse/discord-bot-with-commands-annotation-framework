package dk.simonsejse.discordbot.commands.minecraft;


import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.models.mcreq.McResponse;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;

@Command(
        cmdName = "mcinfo",
        info = "Viser info om en Minecraft spiller udfra brugernavn!",
        cooldown = 1,
        unit = ChronoUnit.MINUTES,
        exclusions = {Role.OWNER},
        parameterNames = {"minecraft_navn"},
        parameterDescriptions = {"spillerens minecraft navn"},
        types = {OptionType.STRING},
        isRequired = {true}


)
public class McInfoCommand implements CommandPerform {

    private final Messages messages;

    @Autowired
    public McInfoCommand(final Messages messages){
        this.messages = messages;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final String id = event.getOptions().get(0).getAsString();
        try {
            URL url = new URL(String.format("https://playerdb.co/api/player/minecraft/%s", id));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            final int responseCode = conn.getResponseCode();

            final InputStream inputStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

            final ObjectMapper mapper = new ObjectMapper();
            final String data = new String(inputStream.readAllBytes());
            final McResponse response = mapper.readValue(data, McResponse.class);

            if (response.isError())
                throw new CommandException("Spilleren findes ikke!");

            final Message mcInfo = this.messages.getMcInfo(response);

            event.deferReply(true).queue(iHook -> iHook.sendMessage(mcInfo).queue());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
