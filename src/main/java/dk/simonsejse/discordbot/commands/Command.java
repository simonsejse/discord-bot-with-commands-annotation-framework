package dk.simonsejse.discordbot.commands;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Command {
    String prefix() default "!";
    String cmdName();
    String info() default "";
    ChronoUnit unit() default ChronoUnit.SECONDS;
    int cooldown() default 0;
}
