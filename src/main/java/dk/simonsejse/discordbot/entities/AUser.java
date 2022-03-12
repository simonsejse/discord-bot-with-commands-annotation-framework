package dk.simonsejse.discordbot.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name="user")
@Getter
@Setter
public class AUser {

    /**
     * Then it is One user to many reports as reporter and also a second One User to many reports as reported
     */

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_seq_gen"
    )
    @SequenceGenerator(
            name="user_id_seq_gen",
            allocationSize = 1
    )
    @Id
    @Column(name = "user_id")
    private long userID;

    @Column(name="guild_id")
    private long guildID;

    @Column(name="jda_user_id")
    private long jdaUserID;

    @Column(name = "user_points")
    private long points;

    @OneToMany(
            fetch=FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "reportedUser",
            orphanRemoval = true
    )
    private List<Report> reports;

    @OneToMany(
            fetch=FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "warned",
            orphanRemoval = true
    )
    private List<Warning> warnings;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Reputation> reputations;

    public AUser(long jdaUserID, long guildID){
        this.jdaUserID = jdaUserID;
        this.guildID = guildID;
        this.points = 0;
        this.reports = new ArrayList<>();
    }

    public AUser(){ }

    public boolean hasUserGivenWithin24Hours(AUser user){
        final Optional<Reputation> any =
                reputations.stream().filter(rep -> rep.getUser().equals(user)).findAny();
        return any.isPresent() && Duration.between(any.get().getWhenGiven(), LocalDateTime.now()).toHours() >= 24;

    }

    public void incrementPoint() {
        ++this.points;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }

    public int getAmountOfWarnings(){
        return this.warnings.size();
    }

    public void addWarning(Warning warning) {
        this.warnings.add(warning);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AUser aUser = (AUser) o;
        return userID == aUser.userID && guildID == aUser.guildID && jdaUserID == aUser.jdaUserID && points == aUser.points && reports.equals(aUser.reports) && warnings.equals(aUser.warnings) && reputations.equals(aUser.reputations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, guildID, jdaUserID, points, reports, warnings, reputations);
    }
}
