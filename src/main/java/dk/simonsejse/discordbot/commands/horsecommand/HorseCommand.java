package dk.simonsejse.discordbot.commands.horsecommand;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
@Command(
        cmdName = "hest",
        info = "Få botten til at sige som en hest!"
)
public class HorseCommand implements CommandPerform {
    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        event.reply("Sig som en hest!").queue();
    }
}
