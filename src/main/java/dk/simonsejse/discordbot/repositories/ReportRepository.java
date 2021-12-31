package dk.simonsejse.discordbot.repositories;

import dk.simonsejse.discordbot.dtos.ReportDTO;
import dk.simonsejse.discordbot.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    //Optional<List<Report>> findAllByReportedUserJdaUserIDAndReportedUserGuildID(long jdaUserID, long guildID);

    Optional<List<ReportDTO>> findAllReportDTOsByReportedUserJdaUserIDAndReportedUserGuildID(long jdaUserID, long guildID);
}
