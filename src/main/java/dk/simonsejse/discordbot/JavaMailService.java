package dk.simonsejse.discordbot;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@EnableScheduling
public class JavaMailService {
    private final Queue<String> errorLogs;
    private final JavaMailSenderImpl mailSender;

    private JavaMailService(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(System.getenv("MAIL_ADDRESS"));
        mailSender.setPassword(System.getenv("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        this.mailSender = mailSender;
        errorLogs = new LinkedBlockingQueue<>();
    }

    @Scheduled(fixedDelay = 60000)
    private void sendExceptionToMail() {
        if (errorLogs.isEmpty()) return;
        final String errorMessage = errorLogs.poll();

        final SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo("simonwa01@gmail.com");
        simpleMessage.setSubject("BOT-DOVER SEVERE:");
        simpleMessage.setText(errorMessage);
        this.mailSender.send(simpleMessage);
    }

    public void addErrorLog(String message){
        this.errorLogs.add(message);
    }
}
