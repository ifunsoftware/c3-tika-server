package org.aphreet.c3.search.tika.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tika.exception.TikaException;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class TikaNotifier {

    public void notifyAboutError(String document, Throwable tikaException){
        final String username = "c3-notifier@ifunsoftware.com";
        final String password = "Not4You!";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("c3-notifier@ifunsoftware.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("mikhail@malygin.me"));
            message.setSubject("Failed to parse document " + document);

            message.setText(formatException(tikaException));

            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String formatException(Throwable e){
        StringWriter sw = new StringWriter();

        PrintWriter writer = new PrintWriter(sw);

        writer.println("Root cause: \n");
        ExceptionUtils.printRootCauseStackTrace(e, writer);

        writer.println("\n\nException stacktrace:\n");
        e.printStackTrace(writer);

        return sw.toString();
    }
}
