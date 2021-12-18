package dk.simonsejse.discordbot.entities;

import jdk.jfr.Timestamp;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Report {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "report_seq_gen"
    )
    @SequenceGenerator(
            name = "report_seq_gen",
            allocationSize = 1
    )
    private int rid;

    @Column(name="reason")
    private String reason;

    @Timestamp
    @Column(name="when_reported")
    private LocalDateTime whenReported;

    @Column(name="reported_by")
    private long reportedBy;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    protected Report(){ }

    public Report(String reason, LocalDateTime whenReported, long reportedBy, final User user) {
        this.reason = reason;
        this.whenReported = whenReported;
        this.reportedBy = reportedBy;
        this.reportedUser = user;
    }

    @Override
    public String toString() {
        return "RID » " + rid + "\nGrund » "+ reason + "\n" + "Dato » "+whenReported+"\n"+"Reported af » "+reportedBy;
    }
}