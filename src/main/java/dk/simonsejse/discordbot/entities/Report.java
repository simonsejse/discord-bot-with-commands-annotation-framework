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
    private long rid;

    @Column(name="reason")
    private String reason;

    @Timestamp
    @Column(name="when_reported")
    private LocalDateTime whenReported;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reported_by_user_id")
    private AUser reportedBy;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private AUser reportedUser;

    protected Report(){ }

    public Report(String reason, LocalDateTime whenReported, AUser reportedBy, final AUser reportedUser) {
        this.reason = reason;
        this.whenReported = whenReported;
        this.reportedBy = reportedBy;
        this.reportedUser = reportedUser;
    }
}