package com.SendMailApp;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

@WebServlet("/SendEmailServlet")
public class SendEmailServlet extends HttpServlet {

    // Replace with your database connection information
    private static final String DB_DRIVER = "your_driver_class";
    private static final String DB_URL = "jdbc:your_database_url";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    // Replace with your SMTP server details
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587; // Replace with appropriate port for your SMTP server
    private static final String SMTP_AUTH = "true"; // Set to "true" if authentication is required
    private static final String SMTP_USER = "nileshgw245@gmail.com";
    private static final String SMTP_PASSWORD = "vami kpze sncm lsaa";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Connect to database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Get staff email IDs with status 0
            String sql = "SELECT mail FROM staffmailid JOIN staffstatus ON staffmailid.staff_id = staffstatus.staff_id WHERE staffstatus.status = 0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Send emails and update status
            while (resultSet.next()) {
                String email = resultSet.getString("mail");
                sendEMail(email);
                updateStaffStatus(connection, email);
            }

            response.getWriter().println("Emails sent successfully and status updated!");
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error sending emails: " + e.getMessage());
        }
    }

    private void sendEMail(String email) throws Exception {

        // Replace with your email content and configuration
        String subject = "Important Message";
        String body = "This is an important message...";

        Properties props = new Properties();
        props.put("mail.smtp.auth", SMTP_AUTH);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                    }
                });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    private void updateStaffStatus(Connection connection, String email) throws Exception {

        String sql = "UPDATE staffstatus SET status = 1 WHERE staff_id = (SELECT staff_id FROM staffmailid WHERE mail = ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
