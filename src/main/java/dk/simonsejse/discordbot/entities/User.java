package dk.simonsejse.discordbot.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {

    @Column(name = "user_id")
    @Id
    private long userId;

    @Column(name = "user_points")
    private long points;

    @OneToMany(
            fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST},
            mappedBy = "reportedUser",
            orphanRemoval = true
    )
    private List<Report> reports;

    @OneToMany(
            fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST},
            mappedBy = "warned",
            orphanRemoval = true
    )
    private List<Warning> warnings;

    public User(long userId){
        this.userId = userId;
        this.points = 0;
        //this.reports = new ArrayList<>();
    }

    public User(){

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
}
