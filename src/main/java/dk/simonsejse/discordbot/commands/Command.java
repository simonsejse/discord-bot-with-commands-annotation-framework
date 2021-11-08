package dk.simonsejse.discordbot.commands;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Command {
    String prefix() default "!";
    String cmdName();
    String info() default "";
    int cooldown() default 0;
}
