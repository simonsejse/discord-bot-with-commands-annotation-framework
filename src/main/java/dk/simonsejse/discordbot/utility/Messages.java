package dk.simonsejse.discordbot.utility;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.infocmd.InfoCommand;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.entities.User;
import dk.simonsejse.discordbot.models.mcreq.McResponse;
import dk.simonsejse.discordbot.models.mcreq.NameHistoryItem;
import dk.simonsejse.discordbot.models.mcreq.Player;
import dk.simonsejse.discordbot.models.weatherreq.Condition;
import dk.simonsejse.discordbot.models.weatherreq.Current;
import dk.simonsejse.discordbot.models.weatherreq.Location;
import dk.simonsejse.discordbot.models.weatherreq.WeatherResponse;
import dk.simonsejse.discordbot.services.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Messages {

    private final UserService userService;

    @Autowired
    public Messages(final UserService userService){
        this.userService = userService;
    }

    public Message getUserReportMessage(List<Report> reports, net.dv8tion.jda.api.entities.User user){
        final int reportSize = reports.size();
        final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(String.format("Reports på %s", user.getAsTag())).setDescription(String.format("Her kan du se en oversigt over report for %s (%d)", user.getAsTag(), user.getIdLong())).setAuthor(user.getId()).setThumbnail(user.getAvatarUrl()).setColor(Colors.PURPLE).setTimestamp(LocalDateTime.now()).setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").addField(new MessageEmbed.Field("Navn", user.getAsMention(), false)).addField(new MessageEmbed.Field("ID", user.getId(), false)).addField(new MessageEmbed.Field("Antal reports", reportSize + (reportSize >= 7 ? " (HØJT)" : reportSize >= 3 ? " (MEDIUM)" : " (LAV)"), false));

        reports.stream().forEach(report -> {
           embedBuilder.addField(
                   new MessageEmbed.Field(
                           String.valueOf(report.getRid()), report.toString()+"\n", false
                   )
           );
        });

        return new MessageBuilder()
                .setEmbed(
                        embedBuilder.build()
                ).build();
    }

    public Message userHasNoSufficientPermission = new MessageBuilder()
            .setEmbed(new EmbedBuilder()
                    .setTitle("Ikke tilstrækkelig tilladelse")
                    .setDescription("Du har desværre ikke tilladelse til at bruge denne kommando!")
                    .setColor(Colors.RED)
                    .setTimestamp(LocalDateTime.now())
                    .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public Message userCreatedInDB(long id) {
        return new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("Oprettet i DB!")
                        .setDescription(String.format("Vi kunne se, at ID: %d ikke var oprettet i bottens database, prøv kommandoen igen!", id))
                        .setColor(Colors.ORANGE)
                        .setTimestamp(LocalDateTime.now())
                        .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
                .build();
    }

    public Message userAlreadyChallengedSomeoneTTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("Fejl")
            .setDescription("Du har allerede udfordret en spiller, du skal enten slette den nuværende udfordring eller vente på at han svarer...")
            .setColor(Colors.RED)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public Message userRegretCancellingTTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("IKKE annulleret!")
            .setDescription("Din udfordring er IKKE blevet annulleret!")
            .setColor(Colors.PINK)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public Message userHasNoChallengeTTT = new MessageBuilder()
            .setEmbed(new EmbedBuilder()
                    .setTitle("Ingen udfordring?")
                    .setDescription("Du har INGEN udfordring lige pt. start en ved brug af kommandoen /startkrydsogbolle. Hvad venter du på! Kom igang! :D ")
                    .setColor(Color.RED)
                    .setTimestamp(LocalDateTime.now())
                    .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                    .build())
            .build();

    public Message getTopTenLeaderBoards(List<User> topTenUsers, JDA jda) {
        final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Top 10 Leaderboards")
                .setDescription("Her ses leaderboards over de største alfa-heste på serveren!")
                .setAuthor("Bot Dover")
                .setColor(Colors.GOLD)
                .setTimestamp(LocalDateTime.now())
                .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256");

        AtomicInteger placement = new AtomicInteger(0);

        Thread awaitEmbeddedFieldsThread = new Thread(() -> {
            for(int i = 0; i < topTenUsers.size(); i++){
                final User user = topTenUsers.get(i);
                final long userId = user.getId().getUserId();


                net.dv8tion.jda.api.entities.User jdaUserById = jda.retrieveUserById(userId).complete();

                final String title = String.format("%d. %s - %d points", placement.incrementAndGet(), jdaUserById.getAsTag(), user.getPoints());
                final String idLine = String.format("ID: %s", userId);
                embedBuilder.addField(title, idLine, false);
            }
        });
        awaitEmbeddedFieldsThread.start();


        try {
            awaitEmbeddedFieldsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new MessageBuilder()
                .setEmbed(embedBuilder
                        .build())
                .build();

    }

    public Message challengeUserInTTT(String user, String opponent){
        return new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("** Udfordring **")
                        .setDescription("Du skal holde op med at se på det, du IKKE HAR, og HUSK HVAD DU KÆMPER FOR! \nHvem VIL du VÆRE FRA DETTE ØJEBLIK FREM? DET ER VÆRD AT KÆMPE FOR!")
                        .addField("Udfordreren:", user, true)
                        .addField("Modstanderen:", opponent, true)
                        .setColor(Colors.BLUE)
                        .setTimestamp(LocalDateTime.now())
                        .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                        .build())
                .build();
    }

    public Message deletedChallengeTTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("Annulleret")
            .setDescription("Din udfordring er blevet annulleret.")
            .setColor(Colors.GREEN)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public Message commandOnCooldownMessage(String cooldown){
        final Command annotation = InfoCommand.class.getAnnotation(Command.class);
        return new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(String.format("Du kan ikke bruge kommandoen /%s endnu!", annotation.cmdName()))
                        .setDescription(String.format("For at formindske spam at kommandoen har vi valgt at tilføje et cooldown på %d %s", annotation.cooldown(), unitToName(annotation.unit())))
                        .setColor(Colors.RED)
                        .setTimestamp(LocalDateTime.now())
                        .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                        .setThumbnail("https://cdn.icon-icons.com/icons2/1380/PNG/512/vcsconflicting_93497.png")
                        .setImage("https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                        .setAuthor("Ups!", "https://botdover.com", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                        .addField("Du skal vente :clock1:", String.format("%s", cooldown), true)
                        .build())
                .build();
    }

    public static final Message TEMPLATE = new MessageBuilder()
            .append("this `supports` __a__ **subset** *of* ~~markdown~~ 😃 ```js\nfunction foo(bar) {\n  console.log(bar);\n}\n\nfoo(1);```")
            .setEmbed(new EmbedBuilder()
                    .setTitle("title ~~(did you know you can have markdown here too?)~~", "https://discordapp.com")
                    .setDescription("this supports [named links](https://discordapp.com) on top of the previously shown subset of markdown. ```\nyes, even code blocks```")
                    .setColor(new Color(65445))
                    .setTimestamp(OffsetDateTime.parse("2021-11-08T07:55:46.969Z"))
                    .setFooter("footer text", "https://cdn.discordapp.com/embed/avatars/0.png")
                    .setThumbnail("https://cdn.discordapp.com/embed/avatars/0.png")
                    .setImage("https://cdn.discordapp.com/embed/avatars/0.png")
                    .setAuthor("author name", "https://discordapp.com", "https://cdn.discordapp.com/embed/avatars/0.png")
                    .addField("🤔", "some of these properties have certain limits...", false)
                    .addField("😱", "try exceeding some of them!", false)
                    .addField("🙄", "an informative error should show up, and this view will remain as-is until all issues are fixed", false)
                    .addField("<:thonkang:219069250692841473>", "these last two", true)
                    .addField("<:thonkang:219069250692841473>", "are inline fields", true)
                    .build())
            .build();

    private static String unitToName(ChronoUnit unit){
        switch(unit){
            case SECONDS:
                return "sekunder";
            case MINUTES:
                return "minutter";
            case HOURS:
                return "timer";
            case DAYS:
                return "dage";
            default:
                return "(fejl)";
        }
    }

    public static final String BORDER_LARGE = "***~~---------------------------------------------------------------------------------------------~~***";
    public static final String BORDER_MEDIUM = "***~~------------------------------------------~~***";
    public static final String BORDER_SMALL = "***~~---------------------~~***";

    public Message successfullyWarnUser(net.dv8tion.jda.api.entities.User warned, net.dv8tion.jda.api.entities.User warnedBy, String reason, LocalDateTime when) {
        final String iconUrl = warned.getAvatarUrl();
        final String warnedAsTag = warned.getAsTag();
        final String warnedByAsTag = warnedBy.getAsTag();
        final long warnedUserId = warned.getIdLong();
        final long warnedByUserId = warnedBy.getIdLong();

        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle("ADVARSEL!")
                .setAuthor(warnedAsTag, "https://www.youtube.com/watch?v=dQw4w9WgXcQ", iconUrl)
                .setDescription(String.format("En advarsel til %s | %d", warnedAsTag, warnedUserId))
                .addField(new MessageEmbed.Field("Advarsel til", String.format("%s", warnedAsTag), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("ID", String.format("%d", warnedUserId), true))
                .addField(new MessageEmbed.Field("Givet af", String.format("%s", warnedByAsTag), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("ID", String.format("%d", warnedByUserId), true))
                .addField(new MessageEmbed.Field("Begrundelse", reason, true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Tidspunkt", when.format(DateFormat.MAIN), true))
                .setThumbnail("https://thumbs.gfycat.com/UniqueSizzlingFinwhale-max-1mb.gif")
                .setColor(Colors.PINK)
                .setTimestamp(LocalDateTime.now())
                .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
                .build();

    }

    public Message getMcInfo(McResponse response) {
        final Player player = response.getData().getPlayer();
        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Minecraft Info")
                .setAuthor(player.getUsername(), player.getAvatar(), player.getAvatar())
                .setDescription(String.format("Information omkring spilleren %s | %s", player.getUsername(), player.getId()))
                .addField(new MessageEmbed.Field("Navn", player.getUsername(), false))

                .addField(new MessageEmbed.Field("ID", player.getId(), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("RID", player.getRawId(), true))
                .addField(new MessageEmbed.Field("Navn Historik:", "\u200b", false));

        for(NameHistoryItem oldName : player.getMeta().getNameHistory()){
            embedBuilder.addField(new MessageEmbed.Field("Navn", oldName.getName(), true));
            embedBuilder.addBlankField(true);
            final long changedToAt = oldName.getChangedToAt();
            embedBuilder.addField(new MessageEmbed.Field(changedToAt == 0 ? "" : "Dato", changedToAt == 0 ? "Nuværende" : LocalDateTime.ofInstant(Instant.ofEpochMilli(changedToAt), ZoneId.systemDefault()).format(DateFormat.MAIN), true));
        }

        return new MessageBuilder().setEmbed(embedBuilder
                .setThumbnail(player.getAvatar())
                .setColor(Colors.PINK)
                .setTimestamp(LocalDateTime.now())
                .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
                .build();
    }

    public Message getWeatherReport(WeatherResponse response) {
        final Location location = response.getLocation();
        final Current current = response.getCurrent();
        final Condition condition = current.getCondition();
        // - //cdn.weatherapi.com/weather/64x64/night/266.png
        // Needs to remove the two // at start and add https://
        final String iconUrl = String.format("https://%s", condition.getIcon().substring(2));

        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setTitle("Vejr rapport")
                .setAuthor(location.getName(), "https://www.youtube.com/watch?v=dQw4w9WgXcQ", iconUrl)
                .setDescription(String.format("%s, %s, %s, latitude %f, longtitude %f, tidszone %s, tidspunkt %s", location.getName(), location.getCountry(), location.getRegion(), location.getLat(), location.getLon(), location.getTzId(), location.getLocaltime()))
                .addField(new MessageEmbed.Field("Sidst opdateret", current.getLastUpdated(), true))
                .addField(new MessageEmbed.Field("Temperatur (celcius)", String.format("%.2f° C", current.getTempC()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Temperatur (fahrenheit)", String.format("%.2f° F", current.getTempF()), true))
                .addField(new MessageEmbed.Field("Dag", current.getIsDay() == 1 ? "dag" : "aften", true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Vejr", condition.getText(), true))
                .addField(new MessageEmbed.Field("Vind (mph)", String.format("%.2f mph", current.getWindMph()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Vind (km/t)", String.format("%.2f km/t", current.getWindKph()), true))
                .addField(new MessageEmbed.Field("Vind (grad)", String.format("%d°", current.getWindDegree()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Vind (retning)", String.format("%s°", current.getWindDir()), true))
                .addField(new MessageEmbed.Field("Tryk i Millibar", String.format("%.2f millibar", current.getPressureMb()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Tryk i inches", String.format("%.2f inches", current.getPressureIn()), true))
                .addField(new MessageEmbed.Field("Nedbørsmængde i millimeter", String.format("%.4f mm", current.getPrecipMm()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Nedbørsmængde i inches", String.format("%.4f in", current.getPrecipIn()), true))
                .addField(new MessageEmbed.Field("Fugtighed (i procent)", String.format("%d%%", current.getHumidity()), true))
                .addBlankField(true)
                .addField(new MessageEmbed.Field("Sky (dækker over himlen)", String.format("%d%%", current.getCloud()), true))
                .addField(new MessageEmbed.Field("Føles som (i grader | i fehrenheit)", String.format("%.2f ° | %.2f °", current.getFeelslikeC(), current.getFeelslikeF()), true))
                .addField(new MessageEmbed.Field("UV Index", String.format("%.2f", current.getUv()), true))
                .addField(new MessageEmbed.Field("Vindstød (mph | kph)", String.format("%.2f mph | %.2f km/t", current.getGustMph(), current.getGustKph()), true))

                .setThumbnail(iconUrl)
                .setColor(Colors.PINK)
                .setTimestamp(LocalDateTime.now())
                .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
                .build();
    }
}
