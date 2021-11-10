package dk.simonsejse.discordbot.utility;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.infocmd.InfoCommand;
import dk.simonsejse.discordbot.commands.tictactoe.StartTicTacToeCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class Messages {
    public static final String BORDER_LARGE = "***~~---------------------------------------------------------------------------------------------~~***";
    public static final String BORDER_MEDIUM = "***~~------------------------------------------~~***";
    public static final String BORDER_SMALL = "***~~---------------------~~***";

    public static final Message ALREADY_CHALLENGED_SOMEONE_TTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("Fejl")
            .setDescription("Du har allerede udfordret en spiller, du skal enten slette den nuværende udfordring eller vente på at han svarer...")
            .setColor(Colors.RED)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public static final Message REGRET_CANCELING_TTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("IKKE annulleret!")
            .setDescription("Din udfordring er IKKE blevet annulleret!")
            .setColor(Colors.PINK)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public static final Message NO_CHALLENGE_TTT = new MessageBuilder()
            .setEmbed(new EmbedBuilder()
                    .setTitle("Ingen udfordring?")
                    .setDescription("Du har INGEN udfordring lige pt. start en ved brug af kommandoen /startkrydsogbolle. Hvad venter du på! Kom igang! :D ")
                    .setColor(Color.RED)
                    .setTimestamp(LocalDateTime.now())
                    .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                    .build())
            .build();

    public static final Message CHALLENGE_TTT(String user, String opponent){
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

    public static final Message DELETED_CHALLENGE_TTT = new MessageBuilder().setEmbed(new EmbedBuilder()
            .setTitle("Annulleret")
            .setDescription("Din udfordring er blevet annulleret.")
            .setColor(Colors.GREEN)
            .setTimestamp(LocalDateTime.now())
            .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256").build())
            .build();

    public static Message commandOnCooldownMessage(String cooldown){
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


}
