package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.cooldown.CooldownManager;
import dk.simonsejse.discordbot.exceptions.CommandCooldownNotExpired;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.utility.Messages;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Component
@Getter
public class CommandHandler extends ListenerAdapter {

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
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        try{
            final long id = event.getUser().getIdLong();
            final Predicate<Map.Entry<Command, CommandPerform>> containsCommand = (entry) -> entry.getKey().cmdName().equalsIgnoreCase(event.getName());
            final Map.Entry<Command, CommandPerform> commandEntry = this.commands.entrySet()
                    .stream()
                    .filter(containsCommand)
                    .findFirst()
                    .orElseThrow(() ->
                            new CommandException("Denne kommando findes ikke!")
                    );

            if (this.cooldownManager.hasCooldownExpired(id, commandEntry.getKey())){
                commandEntry.getValue().perform(event);
                this.cooldownManager.addCooldown(id, commandEntry.getKey());
            }else {
                final String cooldownOnCommand = this.cooldownManager.getCooldown(id, commandEntry.getKey());
                throw new CommandCooldownNotExpired(cooldownOnCommand);
            }
        }catch(CommandException e){
            event.deferReply(true).queue(interactionHook -> {
                interactionHook.sendMessage(e.getMessage()).queue();
            });
        }catch(CommandCooldownNotExpired e){
            event.deferReply(true).queue(interactionHook -> {
                interactionHook
                        .sendMessage(Messages.commandOnCooldownMessage(e.getMessage()))
                        .delay(30, TimeUnit.SECONDS)
                        .queue(message -> {
                            if (message.getReferencedMessage() != null) {
                                message.delete().queueAfter(30, TimeUnit.SECONDS);
                            }
                        });
            });
        }
    }

    /*
    * Old version 1.0
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
                            .flatMap(privateChannel -> {
                                return privateChannel.sendMessage(Messages.commandOnCooldownMessage(e.getMessage()))
                                        .setActionRow(Button.success(ButtonID.ACCEPT_COMMAND_COOLDOWN_EXCEPTION, "Forstået")
                                                .withEmoji(Emoji.fromMarkdown("<:minn:245267426227388416>")));
                            })
                            .delay(30, TimeUnit.SECONDS)
                            .queue(message -> {
                                message.delete().queueAfter(30, TimeUnit.SECONDS);
                            });
                }
            }
        }

    }*/
}
