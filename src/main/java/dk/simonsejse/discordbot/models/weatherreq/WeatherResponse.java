package dk.simonsejse.discordbot.models.weatherreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherResponse {

	@JsonProperty("current")
	private Current current;

	@JsonProperty("location")
	private Location location;

	public Current getCurrent(){
		return current;
	}

	public Location getLocation(){
		return location;
	}

}