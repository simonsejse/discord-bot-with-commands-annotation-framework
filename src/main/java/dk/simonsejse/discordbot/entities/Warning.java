package dk.simonsejse.discordbot.entities;

import jdk.jfr.Timestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Warning {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "warning_sequence_gen"
    )
    @SequenceGenerator(
            name = "warning_sequence_gen",
            allocationSize = 1
    )
    private long wid;

    @Column(name="reason")
    private String reason;

    @Timestamp
    @Column(name="when_reported")
    LocalDateTime whenWarned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="warned_user_id")
    private AUser warned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="warned_by_user_id")
    private AUser warnedBy;

    protected Warning(){ }

    public Warning(String reason, LocalDateTime whenWarned, AUser warned, AUser warnedBy){
        this.reason = reason;
        this.whenWarned = whenWarned;
        this.warned = warned;
        this.warnedBy = warnedBy;
    }
}
