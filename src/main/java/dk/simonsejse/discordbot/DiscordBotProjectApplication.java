package dk.simonsejse.discordbot;

import dk.simonsejse.discordbot.button.ButtonListener;
import dk.simonsejse.discordbot.chat.ChatHandler;
import dk.simonsejse.discordbot.commands.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@SpringBootApplication
@AllArgsConstructor
public class DiscordBotProjectApplication extends ListenerAdapter {

	private final CommandHandler commandHandler;
	private final ButtonListener buttonListener;
	private final ChatHandler chatHandler;

	public static void main(String[] args) {
		final ConfigurableApplicationContext springContext = SpringApplication.run(DiscordBotProjectApplication.class, args);
	}

	@Bean
	public JDA jda() throws LoginException {
		final String discord_bot_token = System.getenv("DISCORD_BOT_TOKEN");
		JDABuilder builder = JDABuilder.createDefault(discord_bot_token, GatewayIntent.GUILD_VOICE_STATES);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.watching("Basketball"));
		builder.addEventListeners(commandHandler, buttonListener, chatHandler, this);
		builder.enableCache(CacheFlag.VOICE_STATE);
		return builder.build();
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event) {
		System.out.println("It joined");

	}
	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		//TODO: Remove getGuildById

		//final JDA jda = event.getJDA(); use jda.upsertCommand() l8 on
		final Guild guild = event.getJDA().getGuildById(689226702861369380L);

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

		System.out.println(commandsData);

		commandsData.forEach(c -> {
			guild.upsertCommand(c).queue();
		});
		//commands.queue();
	}

}
