package dk.simonsejse.discordbot.models.mcreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data{

	@JsonProperty("player")
	private Player player;

	public Player getPlayer(){
		return player;
	}
}