package dk.simonsejse.discordbot.commands.reportcommand;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.dtos.ReportDTO;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.ReportService;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.DateFormat;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Command(
        cmdName = "report",
        info = "A command for reporting people",
        cooldown = 5,
        types = {OptionType.USER, OptionType.STRING},
        isRequired = {true, false},
        parameterNames = {"bruger", "begrundelse"},
        parameterDescriptions = {"brugeren du vil rapportere!", "begrundelse for at rapportere"}
)
public class ReportCommand implements CommandPerform {

    public static final Role REQUIRED_TO_CLEAR_REPORTS = Role.ADMIN;
    public static final Role REQUIRED_TO_BAN = Role.MOD;

    private final ReportService reportService;
    private final UserService userService;
    private final Messages message;
    private final ReportListener reportListener;

    @Autowired
    public ReportCommand(Messages message, ReportService reportService, UserService userService, ReportListener reportListener) {
        this.message = message;
        this.reportService = reportService;
        this.userService = userService;
        this.reportListener = reportListener;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if(event.getGuild() == null) return;
        final List<OptionMapping> options = event.getOptions();
        final long guildID = event.getGuild().getIdLong();
        final long reporteeUserID = event.getUser().getIdLong();
        if (options.size() == 1){
            final net.dv8tion.jda.api.entities.User reportedUserJda = options.get(0).getAsUser();
            final long reportedUserID = reportedUserJda.getIdLong();

            List<ReportDTO> reports = reportService.getReportsByUserId(reportedUserID, guildID);

            if (reports.isEmpty())
                throw new CommandException("Brugeren har ingen anmeldelser!");

            final Message userReportMessage = message.getUserReportMessage(reports, reportedUserJda);
            event.deferReply(false)
                    .queue(interactionHook -> {
                        interactionHook.sendMessage(userReportMessage)
                                .addActionRow(
                                        Button.danger(ButtonID.BAN_PLAYER_ON_REPORT, "Udeluk brugeren!"),
                                        Button.danger(ButtonID.CLEAR_PLAYER_REPORTS, "Fjern anmeldelser")
                                )
                                .queue(m -> m.delete().queueAfter(40, TimeUnit.SECONDS, message -> this.reportListener.removeUserFromListen(reportedUserID)));
                    });

            getExcel(reportedUserJda.getAsTag(), reports, file -> {
                event.getTextChannel().sendFile(file).queue();
            });


            this.reportListener.addUserToListen(reporteeUserID, reportedUserJda);
        }else if (options.size() == 2){
            final User reportedUser = options.get(0).getAsUser();
            final long reportedUserID = reportedUser.getIdLong();
            final String reason = options.get(1).getAsString();

            this.reportService.reportUserById(guildID, reportedUserID, reporteeUserID, reason);

            event.reply(String.format("Du har rapporteret %s med begrundelsen %s", reportedUser.getAsTag(), reason)).queue();
        }
    }

    public void getExcel(String reportedUser, List<ReportDTO> reports, Consumer<File> tempExcelBeforeDeletion){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Anmeldelser");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Row userInfo = sheet.createRow(1);
        Cell userIdTag = userInfo.createCell(1);
        Cell userId = userInfo.createCell(2);
        userIdTag.setCellValue("Reported User");
        userId.setCellValue(reportedUser);

        Row reportCountHeader = sheet.createRow(3);
        Cell reportCountTag = reportCountHeader.createCell(1);
        Cell reportCount = reportCountHeader.createCell(2);
        reportCountTag.setCellValue("Antal Anmeldelser");
        reportCount.setCellValue(reports.size());

        Row headers = sheet.createRow(6);

        Cell header1 = headers.createCell(1);
        header1.setCellValue("Report RID");
        header1.setCellStyle(headerStyle);

        Cell header2 = headers.createCell(2);
        header2.setCellValue("Grund");
        header2.setCellStyle(headerStyle);

        Cell header3 = headers.createCell(3);
        header3.setCellValue("Reported af (ID)");
        header3.setCellStyle(headerStyle);

        Cell header4 = headers.createCell(4);
        header4.setCellValue("Hvornår");
        header4.setCellStyle(headerStyle);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        for(int startRow = 7, index = 0; index < reports.size(); startRow++, index++){
            ReportDTO reportDTO = reports.get(index);
            Row aReport = sheet.createRow(startRow);

            Cell reportRID = aReport.createCell(1);
            reportRID.setCellStyle(style);
            reportRID.setCellValue(reportDTO.getRid());
            Cell reportReason = aReport.createCell(2);
            reportReason.setCellStyle(style);
            reportReason.setCellValue(reportDTO.getReason());
            Cell reportedBy = aReport.createCell(3);
            reportedBy.setCellStyle(style);
            reportedBy.setCellValue(reportDTO.getReportedBy());
            Cell whenReported = aReport.createCell(4);
            whenReported.setCellStyle(style);
            whenReported.setCellValue(reportDTO.getWhenReported().format(DateFormat.MAIN));
            //
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);

        File tempDirectory = FileUtils.getTempDirectory();

        UUID name = null;
        boolean doesNotExist = true;
        while(doesNotExist){
            name = UUID.randomUUID();
            doesNotExist = new File(tempDirectory, String.format("%s.xls", name)).exists();
        }

        File tmpCsv = new File(tempDirectory, String.format("%s.xls", name));

        try {
            tmpCsv.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(tmpCsv)){
            workbook.write(fileOutputStream);
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }finally{
            try {
                workbook.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        tempExcelBeforeDeletion.accept(tmpCsv);
        FileUtils.deleteQuietly(tmpCsv);
    }
}
