package dk.simonsejse.discordbot.models;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("member", 1),
    HELPER("helper", 2),
    MOD("mod", 3),
    STAFF("staff", 4),
    ADMIN("admin", 5),
    OWNER("owner", 6);

    private final String role;
    private final int priority;

    Role(String role, int priority) {
        this.role = role;
        this.priority = priority;
    }
}
