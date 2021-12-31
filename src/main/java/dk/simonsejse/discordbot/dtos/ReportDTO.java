package dk.simonsejse.discordbot.dtos;

import dk.simonsejse.discordbot.entities.AUser;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDTO {
    private long rid;
    private String reason;
    private LocalDateTime whenReported;
    private long reportedBy;

    public ReportDTO(long rid, String reason, LocalDateTime whenReported, AUser reportedBy){
        this.rid = rid;
        this.reason = reason;
        this.whenReported = whenReported;
        this.reportedBy = reportedBy.getJdaUserID();
    }

    @Override
    public String toString() {
        return "RID » " + rid + "\nGrund » "+ reason + "\n" + "Dato » "+whenReported+"\n"+"Reported af » "+reportedBy;
    }
}
