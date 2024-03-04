package com.SendMailApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@WebServlet("/SendEmail")
public class SendEmail extends HttpServlet {

    private static final String SENDER_EMAIL = "nileshgw245@gmail.com"; // Replace with your email address
    private static final String SENDER_PASSWORD = "csrp iecg suiu vqtp"; // Replace with your password (consider using a secure storage mechanism)

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract parameters from the request
        String message = "hello";
        String subject = "test";
        String toUser = "nileshgautam245@dbatu.ac.in";
        String hasAttachment = "N";
        String attachmentPath = "";

        try {
            sendEmail(message, subject, toUser, hasAttachment, attachmentPath);
            response.getWriter().println("Email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            response.getWriter().println("Error sending email.");
            e.printStackTrace();
        }
    }

    private boolean sendEmail(String message, String subject, String toUser, String hasAttachment, String attachmentPath) throws MessagingException {

        // Configure email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");

        // Create a session with authentication
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

        // Create a MimeMessage object
        MimeMessage mimeMessage = new MimeMessage(session);

        // Set message headers
        mimeMessage.setFrom(new InternetAddress(SENDER_EMAIL));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toUser));
        mimeMessage.setSubject(subject);

        // Create the message body
        BodyPart messageBody = new MimeBodyPart();
        messageBody.setContent(message, "text/html; charset=utf-8");

        // Create a multipart message for attachments (if any)
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBody);

        // Attach files if necessary
        if ("Y".equalsIgnoreCase(hasAttachment) && attachmentPath != null && !attachmentPath.isEmpty()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            attachmentPart.setDataHandler(new DataHandler(source));
            String filename = attachmentPath.substring(attachmentPath.lastIndexOf("/") + 1);
            attachmentPart.setFileName(filename);
            multipart.addBodyPart(attachmentPart);
        }

        // Set the message content
        mimeMessage.setContent(multipart);

        // Send the email
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", SENDER_EMAIL, SENDER_PASSWORD);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();

        return true;
    }
}
