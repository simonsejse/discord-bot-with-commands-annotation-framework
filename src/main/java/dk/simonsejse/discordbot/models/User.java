package dk.simonsejse.discordbot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {

    @Id
    @Column(name = "user_id")
    private Long id;


}
