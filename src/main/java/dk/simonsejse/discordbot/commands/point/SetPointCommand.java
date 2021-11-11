package dk.simonsejse.discordbot.commands.point;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(
    cmdName = "setpoint",
    info = "Kommandoen er til staff så de kan give folk point!",
    types = {OptionType.USER, OptionType.INTEGER},
    parameterNames = {"bruger", "antal_point"},
    parameterDescriptions = {"bruger du skal sætte navn på", "hvor mange point brugeren skal have!"},
    isRequired = {true, true},
    roleNeeded = Role.OWNER

)
public class SetPointCommand implements CommandPerform {

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        event.deferReply().queue(interactionHook -> interactionHook.sendMessage("Det virker")
                .queue());
    }
}
