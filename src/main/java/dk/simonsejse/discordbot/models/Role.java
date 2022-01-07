package dk.simonsejse.discordbot.models;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("member", 1),
    HELPER("thicc", 2),
    MOD("london", 3),
    STAFF("bassboosted", 4),
    ADMIN("jøden", 5),
    JEPPE("carboneret citrus vand", 6),
    OWNER("penis", 7);

    private final String role;
    private final int priority;

    Role(String role, int priority) {
        this.role = role;
        this.priority = priority;
    }
}
