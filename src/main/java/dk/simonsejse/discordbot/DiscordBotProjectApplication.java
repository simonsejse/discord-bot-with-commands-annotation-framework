package dk.simonsejse.discordbot;

import dk.simonsejse.discordbot.commands.CommandHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.security.auth.login.LoginException;

@Getter
@SpringBootApplication

public class DiscordBotProjectApplication {

	private JDA jda;

	private final @Autowired CommandHandler commandHandler;

	public DiscordBotProjectApplication(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	public static void main(String[] args) {
		SpringApplication.run(DiscordBotProjectApplication.class, args);
	}

	@Bean
	public JDA getJDA() throws LoginException {
		JDABuilder builder = JDABuilder.createDefault(TokenUtil.TOKEN);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.watching("Mia Khalifa getting pounded by"));

		builder.addEventListeners(commandHandler);

		return builder.build();
	}

}
