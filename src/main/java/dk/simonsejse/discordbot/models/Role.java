package dk.simonsejse.discordbot.models;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("member", 1),
    MOD("mod", 2),
    STAFF("staff", 3),
    ADMIN("admin", 4),
    OWNER("owner", 5);

    public final String role;
    public final int priority;

    Role(String role, int priority) {
        this.role = role;
        this.priority = priority;
    }
}
