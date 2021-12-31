package dk.simonsejse.discordbot.commands;


import dk.simonsejse.discordbot.models.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Command {
    String cmdName();
    String info();
    ChronoUnit unit() default ChronoUnit.SECONDS;
    int cooldown() default 0;

    OptionType[] types() default {};
    String[] parameterNames() default {};
    String[] parameterDescriptions() default {};
    boolean[] isRequired() default {};

    Role roleNeeded() default Role.MEMBER;
    Role[] exclusions() default {};
}