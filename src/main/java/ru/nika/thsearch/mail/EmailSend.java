package ru.nika.thsearch.mail;

import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.nika.thsearch.selenium.TdValue;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.*;
import javax.sound.midi.Soundbank;

/**
 * @author Marat Sadretdinov
 */

public class EmailSend {



    public void mailSend(String send, Logger logger, Map<String,String> map, String title) {

        try {
            logger.info((new Date()) + "---- Email-send ---");
            Properties props = System.getProperties();
            props.put("mail.smtps.host", map.get("mailHost").trim());
            props.put("mail.smtps.port", map.get("mailPort").trim());
            props.put("mail.smtps.auth", map.get("mailSmtpAuth").trim());
            Session session = Session.getInstance(props, null);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(map.get("mailFromEmail").trim()));
            msg.setSubject(title);
            msg.setText(send);
            msg.setHeader("Content-Transfer-Encoding", "base64");
            msg.setSentDate(new Date());
            SMTPTransport t =
                    (SMTPTransport) session.getTransport("smtps");
            try {
                t.connect(map.get("mailHost").trim(), map.get("mailUser").trim(), map.get("mailPassword").trim());
            } catch (AuthenticationFailedException authenticationFailedException) {
                logger.info("Не удалось отправить почту. Ошибка авторизации. Проверьте правильность введенных данных: email отправителя, логин и пароль.");
            }
            String[] mailList = map.get("mailToEmail").trim().split(",");
            for (String email : mailList) {
                try {
                    msg.setRecipients(Message.RecipientType.CC,
                            InternetAddress.parse(email.trim(), false));
                    t.sendMessage(msg, msg.getAllRecipients());
                    logger.info("Response " + email + ": " + t.getLastServerResponse());
                } catch (SendFailedException sendFailedException) {
                    logger.info("Не удалось отправить почту. Не верно указан Email " + email + "получателя.");
                } catch (IllegalStateException illegalStateException) {
                    logger.info("Не удалось отправить почту. Not connected");
                }
            }
            t.close();

        } catch (MessagingException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
