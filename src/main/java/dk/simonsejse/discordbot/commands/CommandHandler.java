package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.cooldown.CooldownManager;
import dk.simonsejse.discordbot.exceptions.CommandCooldownNotExpired;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.UserNoSufficientPermission;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;


@Component
@Getter
public class CommandHandler extends ListenerAdapter {

    private Map<Command, CommandPerform> commands;

    //DI
    private final CooldownManager cooldownManager;
    private final UserService userService;
    private final Messages messages;

    @Autowired(required = false)
    public CommandHandler(List<CommandPerform> commandPerformList, CooldownManager cooldownManager, UserService userService, Messages messages) {
        this.messages = messages;
        setupCommands(commandPerformList);
        this.cooldownManager = cooldownManager;
        this.userService = userService;
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
        final long id = event.getUser().getIdLong();
        if (event.getUser().getIdLong() == 479363759895216160L) return;
        try{
            //Increment points for each command
            this.userService.incrementUserPointByUserId(id, event.getGuild().getIdLong());

            final Predicate<Map.Entry<Command, CommandPerform>> containsCommand = (entry) -> entry.getKey().cmdName().equalsIgnoreCase(event.getName());
            final Map.Entry<Command, CommandPerform> commandEntry = this.commands.entrySet()
                    .stream()
                    .filter(containsCommand)
                    .findFirst()
                    .orElseThrow(() ->
                            new CommandException("Denne kommando findes ikke!")
                    );

            final Member member = event.getGuild().retrieveMember(event.getUser()).complete();

            final Role[] exclusions = commandEntry.getKey().exclusions();

            final Set<String> roles = member.getRoles()
                    .stream()
                    .map(net.dv8tion.jda.api.entities.Role::getName)
                    .collect(toSet());


            final boolean isExcluded = Arrays.stream(exclusions)
                    .map(Role::getRole)
                    .anyMatch(roles::contains);


            if (!(event.getUser().getIdLong() == 292418783547490314L) && !isExcluded && !this.cooldownManager.hasCooldownExpired(id, commandEntry.getKey())) {
                final String cooldownOnCommand = this.cooldownManager.getCooldown(id, commandEntry.getKey());
                throw new CommandCooldownNotExpired(cooldownOnCommand);
            }

            if (!(event.getUser().getIdLong() == 292418783547490314L) && !isExcluded && !userService.doesMemberHaveSufficientRole(member, commandEntry.getKey().roleNeeded()))
                throw new UserNoSufficientPermission();

            this.cooldownManager.addCooldown(id, commandEntry.getKey());
            commandEntry.getValue().perform(event);

        }catch(CommandException e){
            if (e.getMessage() == null) return;
            event.deferReply(e.isDeferReply()).queue(interactionHook -> {
                interactionHook.sendMessage(e.getMessage()).queue();
            });
        }catch(CommandCooldownNotExpired e){
            event.deferReply(true).queue(interactionHook -> {
                interactionHook
                        .sendMessage(this.messages.commandOnCooldownMessage(e.getMessage()))
                        .delay(30, TimeUnit.SECONDS)
                        .queue(message -> {
                            if (message.getReferencedMessage() != null) {
                                message.delete().queueAfter(30, TimeUnit.SECONDS);
                            }
                        });
            });
        } catch (UserNoSufficientPermission userNoSufficientPermission) {
            event.deferReply(false).queue(interactionHook -> {
                interactionHook
                        .sendMessage(this.messages.userHasNoSufficientPermission)
                        .queue();
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
