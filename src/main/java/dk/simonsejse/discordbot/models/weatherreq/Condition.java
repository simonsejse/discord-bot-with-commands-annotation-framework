package dk.simonsejse.discordbot.models.weatherreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition{

	@JsonProperty("code")
	private int code;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("text")
	private String text;

	public int getCode(){
		return code;
	}

	public String getIcon(){
		return icon;
	}

	public String getText(){
		return text;
	}
}