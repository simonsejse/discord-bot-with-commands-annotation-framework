package dk.simonsejse.discordbot.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public AUser(long jdaUserID, long guildID){
        this.jdaUserID = jdaUserID;
        this.guildID = guildID;
        this.points = 0;
        this.reports = new ArrayList<>();
    }

    public AUser(){ }

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
}
