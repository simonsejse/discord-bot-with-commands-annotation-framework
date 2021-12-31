package dk.simonsejse.discordbot.commands.navn;

import com.sun.istack.Nullable;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.NameInfoGetRequestMisMatchException;
import dk.simonsejse.discordbot.utility.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Command(
        cmdName = "navn",
        info = "Viser statistik for navne i Danmark",
        types = {OptionType.STRING, OptionType.STRING},
        parameterNames = {"navn", "efternavn"},
        parameterDescriptions = {"personens navn", "personens efternavn"},
        isRequired = {true, false}
)
public class NameInfoCommand implements CommandPerform {

    private static final String BASE_URL = "https://www.dst.dk/da/Statistik/emner/borgere/navne/HvorMange?ajax=1&";

    private Command command;

    public NameInfoCommand(){
        this.command = getClass().getAnnotation(Command.class);
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final OptionMapping name = event.getOption(command.parameterNames()[0]);
        final OptionMapping lastName = event.getOption(command.parameterNames()[1]);

        try {
            String html = sendHtmlGetRequest(name, lastName);

            final Document parse = Jsoup.parse(html);

            final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Navn Statistik").setDescription("Webscrape fra dst.dk");
            embedBuilder.setThumbnail("https://media4.giphy.com/media/l3978y5HqiEtqupiM/giphy.gif");
            embedBuilder.setColor(Colors.PINK);

            final Elements trElements = parse.select("tr");
           //Tr size=3
            final List<String> titles = new ArrayList<>();
            final List<String> categories = new ArrayList<>();
            trElements.forEach(element -> {
                final Elements thElements = element.select("th");
                thElements.stream().map(Element::text).forEach(titles::add);
                final Elements tdElements = element.select("td");
                tdElements.stream().map(Element::text).forEach(categories::add);
            });
            addFields(embedBuilder, titles);
            addFields(embedBuilder, null);
            addFields(embedBuilder, categories);

            event.deferReply(false).queue(iHook -> {
                iHook.sendMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build()).queue();
            });
        } catch (IOException | NameInfoGetRequestMisMatchException e) {
            event.deferReply(true).queue(iHook -> {
                iHook.sendMessage(e.getMessage()).queue();
            });
        }

    }

    private void addFields(EmbedBuilder embedBuilder, List<String> elem) {
        if (elem == null){
            embedBuilder.addField("⤜═════════════════════════════════⤛", " ", false);
            return;
        }
        final int size = elem.size();
        for(int i = 0; i < size; i++){
            if (i%4 == 1){
                String val = String.format("%s | %s", elem.get(i), elem.get(++i));
                embedBuilder.addField(val, " ", true);
                continue;
            }
            embedBuilder.addField(elem.get(i), " ", true);
        }
    }


    private String sendHtmlGetRequest(@Nullable OptionMapping name, @Nullable OptionMapping lastName) throws IOException, NameInfoGetRequestMisMatchException {
        final String firstName = String.format("firstName=%s", (name != null ? name.getAsString() : ""));
        final String lName = String.format("lastName=%s", (lastName != null ? lastName.getAsString() : ""));
        final URL url = new URL(BASE_URL + firstName + "&" + lName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200){
            final InputStream inputStream = conn.getInputStream();
            return new String(inputStream.readAllBytes());
        }
        throw new NameInfoGetRequestMisMatchException("Der blev ikke fundet noget med navene...");
    }
}
