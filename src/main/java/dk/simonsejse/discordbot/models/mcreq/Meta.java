package dk.simonsejse.discordbot.models.mcreq;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta{

	@JsonProperty("name_history")
	private List<NameHistoryItem> nameHistory;

	public List<NameHistoryItem> getNameHistory(){
		return nameHistory;
	}
}