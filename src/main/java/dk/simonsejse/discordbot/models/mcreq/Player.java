package dk.simonsejse.discordbot.models.mcreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player{

	@JsonProperty("raw_id")
	private String rawId;

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("id")
	private String id;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("username")
	private String username;

	public String getRawId(){
		return rawId;
	}

	public Meta getMeta(){
		return meta;
	}

	public String getId(){
		return id;
	}

	public String getAvatar(){
		return avatar;
	}

	public String getUsername(){
		return username;
	}
}