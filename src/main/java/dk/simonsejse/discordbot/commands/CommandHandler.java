package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.cooldown.CooldownManager;
import dk.simonsejse.discordbot.exceptions.CommandCooldownNotExpired;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
public class CommandHandler implements EventListener {

    private Map<Command, CommandPerform> commands;
    private final CooldownManager cooldownManager;

    @Autowired(required = false)
    public CommandHandler(List<CommandPerform> commandPerformList, CooldownManager cooldownManager) {
        setupCommands(commandPerformList);
        this.cooldownManager = cooldownManager;
    }


    private void setupCommands(List<CommandPerform> commandPerformList){
        this.commands = commandPerformList.stream()
                .collect(Collectors.toMap(commandPerform ->
                                commandPerform.getClass().getAnnotation(Command.class),
                                commandPerform -> commandPerform)
                );
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent){
            final MessageReceivedEvent messageEvent = (MessageReceivedEvent) genericEvent;
            final User author = messageEvent.getAuthor();
            long id = author.getIdLong();

            final Optional<CommandPerform> commandOptional = commands
                    .entrySet()
                    .stream()
                    .filter(commandEntry -> {
                        final String contentRaw = messageEvent.getMessage().getContentRaw();
                        final Command cmd = commandEntry.getKey();
                        return contentRaw.startsWith(String.format("%s%s", cmd.prefix(), cmd.cmdName()));
                    })
                    .map(Map.Entry::getValue)
                    .findFirst();

            if(commandOptional.isPresent()){
                try{
                    final CommandPerform commandPerform = commandOptional.get();
                    final Command command = commandPerform.getClass().getAnnotation(Command.class);
                    if (this.cooldownManager.hasCooldownExpired(id, command)){
                        commandPerform.perform(messageEvent);
                        this.cooldownManager.addCooldown(id, command);
                    }else {
                        final String cooldownOnCommand = this.cooldownManager.getCooldown(id, command);
                        throw new CommandCooldownNotExpired(cooldownOnCommand);
                    }
                }catch(CommandException e){
                    author.openPrivateChannel()
                            .flatMap(privateChannel -> privateChannel.sendMessage(e.getMessage()))
                            .queue();
                }catch(CommandCooldownNotExpired e){
                    author.openPrivateChannel()
                            .flatMap(privateChannel -> privateChannel.sendMessage(Messages.commandOnCooldownMessage(e.getMessage())))
                            .delay(30, TimeUnit.SECONDS)
                            .flatMap(Message::delete)
                            .queue();
                }
            }

        }
    }
}
