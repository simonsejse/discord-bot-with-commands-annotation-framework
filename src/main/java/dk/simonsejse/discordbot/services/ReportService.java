package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.dtos.ReportDTO;
import dk.simonsejse.discordbot.entities.AUser;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

//Spring AOP

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }


    /**
     *
     * @param reportedUserID Reported User id
     * @return a List<Report> of the Users reports
     */
    public List<ReportDTO> getReportsByUserId(long reportedUserID, long guildID) {
        return this.reportRepository.findAllReportDTOsByReportedUserJdaUserIDAndReportedUserGuildID(reportedUserID, guildID).orElseGet(() -> {
            this.userService.createNewUserByID(reportedUserID, guildID);
            return Collections.emptyList();
        });
    }

    /**
     *
     * @param userId User id
     */
    @Transactional
    public void clearAllReportsByUserId(long jdaUserID, long guildID) {
        final AUser user = userService.getUserByJDAUserIDAndGuildIDFetchReports(jdaUserID, guildID);
        user.getReports().clear();
    }

    /**
     *
     * @param id User id
     * @param reason The report reason
     * @param reportedBy The username of who reported
     */
    @Transactional
    public void reportUserById(long guildID, long reportedUserID, long reportedByUserID, String reason) {
        final AUser reportedUser = userService.getUserByJDAUserIDAndGuildIDFetchReports(reportedUserID, guildID);
        final AUser reportedByAUser = userService.getUserByID(reportedByUserID, guildID);
        reportedUser.addReport(new Report(reason, LocalDateTime.now(), reportedByAUser, reportedUser));
    }


}
