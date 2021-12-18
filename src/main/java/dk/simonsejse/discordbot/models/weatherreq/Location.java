package dk.simonsejse.discordbot.models.weatherreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location{

	@JsonProperty("localtime")
	private String localtime;

	@JsonProperty("country")
	private String country;

	@JsonProperty("localtime_epoch")
	private int localtimeEpoch;

	@JsonProperty("name")
	private String name;

	@JsonProperty("lon")
	private double lon;

	@JsonProperty("region")
	private String region;

	@JsonProperty("lat")
	private double lat;

	@JsonProperty("tz_id")
	private String tzId;

	public String getLocaltime(){
		return localtime;
	}

	public String getCountry(){
		return country;
	}

	public int getLocaltimeEpoch(){
		return localtimeEpoch;
	}

	public String getName(){
		return name;
	}

	public double getLon(){
		return lon;
	}

	public String getRegion(){
		return region;
	}

	public double getLat(){
		return lat;
	}

	public String getTzId(){
		return tzId;
	}
}