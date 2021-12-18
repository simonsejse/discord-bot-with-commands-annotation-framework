package dk.simonsejse.discordbot.models.mcreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NameHistoryItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("changedToAt")
	private long changedToAt;

	public String getName(){
		return name;
	}

	public long getChangedToAt(){
		return changedToAt;
	}
}