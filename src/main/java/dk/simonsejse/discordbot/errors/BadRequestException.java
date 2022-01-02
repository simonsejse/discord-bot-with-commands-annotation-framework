package dk.simonsejse.discordbot.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
public class BadRequestException {
    @JsonProperty(value = "status")
    private int statusCode;
    @JsonProperty(value = "message")
    private String errorMsg;
    @JsonProperty(value = "error")
    private String msg;
    protected BadRequestException(){}
}
