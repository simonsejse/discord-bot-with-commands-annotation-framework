package dk.simonsejse.discordbot.models.weatherreq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Current{

	@JsonProperty("feelslike_c")
	private double feelslikeC;

	@JsonProperty("uv")
	private double uv;

	@JsonProperty("last_updated")
	private String lastUpdated;

	@JsonProperty("feelslike_f")
	private double feelslikeF;

	@JsonProperty("wind_degree")
	private int windDegree;

	@JsonProperty("last_updated_epoch")
	private int lastUpdatedEpoch;

	@JsonProperty("is_day")
	private int isDay;

	@JsonProperty("precip_in")
	private double precipIn;

	@JsonProperty("wind_dir")
	private String windDir;

	@JsonProperty("gust_mph")
	private double gustMph;

	@JsonProperty("temp_c")
	private double tempC;

	@JsonProperty("pressure_in")
	private double pressureIn;

	@JsonProperty("gust_kph")
	private double gustKph;

	@JsonProperty("temp_f")
	private double tempF;

	@JsonProperty("precip_mm")
	private double precipMm;

	@JsonProperty("cloud")
	private int cloud;

	@JsonProperty("wind_kph")
	private double windKph;

	@JsonProperty("condition")
	private Condition condition;

	@JsonProperty("wind_mph")
	private double windMph;

	@JsonProperty("vis_km")
	private double visKm;

	@JsonProperty("humidity")
	private int humidity;

	@JsonProperty("pressure_mb")
	private double pressureMb;

	@JsonProperty("vis_miles")
	private double visMiles;

	public double getFeelslikeC(){
		return feelslikeC;
	}

	public double getUv(){
		return uv;
	}

	public String getLastUpdated(){
		return lastUpdated;
	}

	public double getFeelslikeF(){
		return feelslikeF;
	}

	public int getWindDegree(){
		return windDegree;
	}

	public int getLastUpdatedEpoch(){
		return lastUpdatedEpoch;
	}

	public int getIsDay(){
		return isDay;
	}

	public double getPrecipIn(){
		return precipIn;
	}

	public String getWindDir(){
		return windDir;
	}

	public double getGustMph(){
		return gustMph;
	}

	public double getTempC(){
		return tempC;
	}

	public double getPressureIn(){
		return pressureIn;
	}

	public double getGustKph(){
		return gustKph;
	}

	public double getTempF(){
		return tempF;
	}

	public double getPrecipMm(){
		return precipMm;
	}

	public int getCloud(){
		return cloud;
	}

	public double getWindKph(){
		return windKph;
	}

	public Condition getCondition(){
		return condition;
	}

	public double getWindMph(){
		return windMph;
	}

	public double getVisKm(){
		return visKm;
	}

	public int getHumidity(){
		return humidity;
	}

	public double getPressureMb(){
		return pressureMb;
	}

	public double getVisMiles(){
		return visMiles;
	}

}