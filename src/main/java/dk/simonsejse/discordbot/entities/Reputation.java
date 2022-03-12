package dk.simonsejse.discordbot.entities;

import com.sun.istack.Nullable;
import jdk.jfr.BooleanFlag;
import jdk.jfr.Timestamp;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "user_reputation")
@Entity
@Getter

public class Reputation {

    //Skal huske at en bruger ikke kan lave mange request til en
    @Id
    private long repid;

    @Column(name = "is_rep_negative")
    private boolean isPositive;

    @Nullable
    @Column(name="optional_rep_comment")
    private String comment;

    @Timestamp
    private LocalDateTime whenGiven;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private AUser user;

    @ManyToOne
    private AUser gaveRep;


}
