package dk.simonsejse.discordbot.utility;

import java.time.temporal.ChronoUnit;

public class UnitConverter {
    public static String toName(ChronoUnit unit){
        switch(unit){
            case SECONDS:
                return "sekunder";
            case MINUTES:
                return "minutter";
            case HOURS:
                return "timer";
            case DAYS:
                return "dage";
            default:
                return "(fejl)";
        }
    }
}
