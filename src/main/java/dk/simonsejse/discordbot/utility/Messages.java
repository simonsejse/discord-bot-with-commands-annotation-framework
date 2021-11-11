package dk.simonsejse.discordbot.utility;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.infocmd.InfoCommand;
import dk.simonsejse.discordbot.models.User;
import dk.simonsejse.discordbot.services.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class Messages {

    private final UserService userService;

    @Autowired
    public Messages(final UserService userService){
        this.userService = userService;
    }

    public Message userCreatedInDB = new MessageBuilder()
            .setEmbed(new EmbedBuilder()
            .setTitle("Oprettet i DB!")
            .setDescription("Vi kunne se, at du ikke var oprettet i bottens database, prøv kommandoen igen!")
            .setColor(Colors.ORANGE)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

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

        for(int i = 0; i < topTenUsers.size(); i++){
            final User user = topTenUsers.get(i);
            final long userId = user.getId();

            jda.retrieveUserById(userId).queue(jdaUserById -> {
                final String title = String.format("%d. %s - %d points", placement.incrementAndGet(), jdaUserById.getAsTag(), user.getPoints());
                final String idLine = String.format("ID: %s", user.getId());
                embedBuilder.addField(title, idLine, false);
            });
        }

        try {
            completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
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

}
