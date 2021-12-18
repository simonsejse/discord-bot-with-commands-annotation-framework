package dk.simonsejse.discordbot.models.mcreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class McResponse {

	@JsonProperty("code")
	private String code;

	@JsonProperty("data")
	private Data data;

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("error")
	private boolean error;

	@JsonProperty("message")
	private String message;

	public String getCode(){
		return code;
	}

	public Data getData(){
		return data;
	}

	public boolean isSuccess(){
		return success;
	}

	public boolean isError() { return error; }

	public String getMessage(){
		return message;
	}
}