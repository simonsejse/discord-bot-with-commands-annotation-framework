package dk.simonsejse.discordbot.cooldown;

import dk.simonsejse.discordbot.commands.Command;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class CooldownManager {
    private final Map<Long, List<CooldownCommand>> commandsOnCooldown;

    public CooldownManager(){
        this.commandsOnCooldown = new HashMap<>();
    }

    public void addCooldown(Long id, Command command){
        if (this.commandsOnCooldown.containsKey(id)) {
            this.commandsOnCooldown.computeIfPresent(id, (currentId, currentCommandsOnCooldown) -> {
                final List<CooldownCommand> cooldownCommands = new LinkedList<>(currentCommandsOnCooldown);
                cooldownCommands.add(new CooldownCommand(command, LocalDateTime.now().plus(command.cooldown(), command.unit())));
                return cooldownCommands;
            });
        }else {
            final List<CooldownCommand> cooldownCommands = new ArrayList<>();
            cooldownCommands.add(new CooldownCommand(command, LocalDateTime.now().plus(command.cooldown(), command.unit())));
            this.commandsOnCooldown.put(id, cooldownCommands);
        }
    }

    public boolean hasCooldownExpired(Long id, Command command){
        if (!commandsOnCooldown.containsKey(id)) return true;
        final List<CooldownCommand> cooldownCommands = this.commandsOnCooldown.get(id);
        return cooldownCommands.stream().noneMatch(cooldownCommand -> cooldownCommand.getCommand().equals(command))
                || cooldownCommands.removeIf(cooldownCommand -> LocalDateTime.now().isAfter(cooldownCommand.getWhenExecuted()));
    }

    public String getCooldown(Long id, Command command){
        if (this.commandsOnCooldown.containsKey(id)){
            final List<CooldownCommand> cooldownCommands = this.commandsOnCooldown.get(id);
            final LocalDateTime localDateTime = cooldownCommands.stream().filter(cooldownCommand -> cooldownCommand.getCommand().equals(command))
                    .map(CooldownCommand::getWhenExecuted)
                    .findFirst()
                    .orElse(LocalDateTime.now());
            final Duration between = Duration.between(LocalDateTime.now(), localDateTime);

            return String.format("%dd:%dt:%02dm:%02ds", between.toDaysPart(), between.toHoursPart(), between.toMinutesPart(), between.toSecondsPart());
        }
        return "0";
    }

}
