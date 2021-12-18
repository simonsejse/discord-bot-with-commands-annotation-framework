package dk.simonsejse.discordbot;

import dk.simonsejse.discordbot.button.ButtonListener;
import dk.simonsejse.discordbot.chat.ChatHandler;
import dk.simonsejse.discordbot.commands.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@SpringBootApplication
@AllArgsConstructor
public class DiscordBotProjectApplication {

	private final CommandHandler commandHandler;
	private final ButtonListener buttonListener;
	private final ChatHandler chatHandler;

	public static void main(String[] args) {
		SpringApplication.run(DiscordBotProjectApplication.class, args);
	}

	@Bean
	public JDA jda() throws LoginException {
		JDABuilder builder = JDABuilder.createDefault(TokenUtil.TOKEN);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.watching("pounded"));
		builder.addEventListeners(commandHandler, buttonListener, chatHandler);

		JDA jda = builder.build();

		try {
			jda.awaitReady();
//
			//859960986014187560
			final CommandListUpdateAction commands = jda.getGuildById("689226702861369380").updateCommands();

			final List<CommandData> commandsData = commandHandler
					.getCommands()
					.keySet()
					.stream()
					.map(command -> {
						final CommandData commandData = new CommandData(command.cmdName(), command.info());
						for(int i = 0; i < command.types().length
								&& i < command.parameterNames().length
								&& i < command.parameterDescriptions().length
								&& i < command.isRequired().length; i++){
							commandData.addOptions(new OptionData(command.types()[i], command.parameterNames()[i], command.parameterDescriptions()[i], command.isRequired()[i]));
						}
						return commandData;
					}).collect(Collectors.toList());


			commands.addCommands(commandsData);

			commands.queue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return jda;
	}





}
