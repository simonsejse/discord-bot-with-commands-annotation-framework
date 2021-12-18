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
    @JoinColumn(name="warned")
    private User warned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="warned_by")
    private User warnedBy;

    protected Warning(){ }

    public Warning(String reason, LocalDateTime whenWarned, User warned, User warnedBy){
        this.reason = reason;
        this.whenWarned = whenWarned;
        this.warned = warned;
        this.warnedBy = warnedBy;
    }
}
