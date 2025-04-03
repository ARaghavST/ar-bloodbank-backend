package com.ar.bloodbank.helpers;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailFunctions {

    private static String PASSWORD, USERNAME, HOST, PORT;

    public EmailFunctions() {
        Dotenv dotenv = Dotenv.load();

        USERNAME = dotenv.get("MAIL_AUTH_USER");
        PASSWORD = dotenv.get("MAIL_AUTH_PASSWORD");
        HOST = dotenv.get("MAIL_HOST");
        PORT = dotenv.get("MAIL_PORT");
    }

    public boolean sendEmail(String targetEmail, String targetName, String targetPassword) {

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        // below line is for approval of successful handshake
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };

        // We are creating an email session , with above properties , auth
        // Session is actually a timed work , means it is a type of task which should be completed within a time period , ex: 10 seconds
        // Session is for 2 way handshake
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        String body = "Dear " + targetName + ",\n"
                + "\n"
                + "Great news! Your blood donor registration has been approved, and we’re excited to have you as part of our lifesaving community.\n"
                + "\n"
                + "As an approved donor, you will need the following secret password for verification during your visits:\n"
                + "\n"
                + "🔒 Your Secret Password: " + targetPassword + "\n"
                + "\n"
                + "Please keep this password safe and do not share it with anyone. You’ll need it for identity confirmation when donating blood.\n"
                + "\n"
                + "If you want to change the password, then it can be done in your profile page after logging in."
                + "\n"
                + "See you at the donation center soon!\n"
                + "\n"
                + "Best regards,\n"
                + "BloodBank Admin\n"
                + "arbloodbank.1201@gmail.com";

        try {

            // pre-defined functionality
            msg.setFrom(new InternetAddress(USERNAME));

            InternetAddress[] toAddresses = {new InternetAddress(targetEmail)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject("Your Donor Approval is Confirmed – Let’s Save Lives Together!");
            msg.setText(body);

            // sends the e-mail
            Transport.send(msg);

            return true;
        } catch (MessagingException m) {
            System.out.println("Exception occured in sending mail : " + m.getMessage());
        }
        return false;
    }

    /**
     * This function will send email to donors for blood request
     *
     *
     * @param targetEmail
     * @param targetName
     * @param bloodGroupNeeded
     *
     * @return true OR false
     *
     */
    public boolean sendBloodRequestEmail(String targetEmail, String targetName, String bloodGroupNeeded) {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        // below line is for approval of successful handshake
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };

        // We are creating an email session , with above properties , auth
        // Session is actually a timed work , means it is a type of task which should be completed within a time period , ex: 10 seconds
        // Session is for 2 way handshake
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        String body = "Dear " + targetName + ",\n"
                + "\n"
                + "I hope this email finds you well. We are reaching out with an urgent request for blood donation. Your generosity and willingness to donate can help save lives.\n"
                + "\n"
                + "Currently, we are in need of [" + bloodGroupNeeded + "] for a patient in critical condition. Your past contributions have made a significant impact, and we kindly ask if you would consider donating again."
                + "\n"
                + "Please raise a donation request from your profile, and provide an availability date, and we can proceed for blood donation."
                + "For any questions or to confirm your donation, please reply to this email or contact us at arbloodbank.1201@gmail.com."
                + "\n"
                + "Best regards,\n"
                + "BloodBank Admin\n"
                + "arbloodbank.1201@gmail.com";

        try {

            // pre-defined functionality
            msg.setFrom(new InternetAddress(USERNAME));

            InternetAddress[] toAddresses = {new InternetAddress(targetEmail)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject("Urgent Blood Donation Needed !");
            msg.setText(body);

            // sends the e-mail
            Transport.send(msg);

            return true;
        } catch (MessagingException m) {
            System.out.println("Exception occured in sending mail : " + m.getMessage());
        }
        return false;
    }

    public boolean sendSignupTestEmail(String targetEmail, String targetName) {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        // below line is for approval of successful handshake
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };

        // We are creating an email session , with above properties , auth
        // Session is actually a timed work , means it is a type of task which should be completed within a time period , ex: 10 seconds
        // Session is for 2 way handshake
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        String body = "Dear " + targetName + ",\n"
                + "\n"
                + "Your Signup request for BloodBank have been received."
                + "\n"
                + "We request you to come to our branch for giving a short medical eligibility test, in coming 2 days"
                + "\n"
                + "Address : ABC Street, New Delhi"
                + "\n"
                + "Best regards,\n"
                + "BloodBank Admin\n"
                + "arbloodbank.1201@gmail.com";

        try {

            // pre-defined functionality
            msg.setFrom(new InternetAddress(USERNAME));

            InternetAddress[] toAddresses = {new InternetAddress(targetEmail)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject("SignUP Test approval");
            msg.setText(body);

            // sends the e-mail
            Transport.send(msg);

            return true;
        } catch (MessagingException m) {
            System.out.println("Exception occured in sending mail : " + m.getMessage());
        }
        return false;
    }

    public boolean sendReceiverApprovalMail(String targetName, String targetEmail, Double bloodAmount, String bloodGroup) {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");
        // below line is for approval of successful handshake
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };

        // We are creating an email session , with above properties , auth
        // Session is actually a timed work , means it is a type of task which should be completed within a time period , ex: 10 seconds
        // Session is for 2 way handshake
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        String body = "Dear " + targetName + ",\n"
                + "\n"
                + "Your request for blood is completed."
                + "\n"
                + "Blood Amount : " + bloodAmount + " mL"
                + "\n"
                + "Blood Group : " + bloodGroup
                + "\n"
                + "We request you to come to our branch, to receive it."
                + "\n"
                + "Thanks"
                + "\n"
                + "Address : ABC Street, New Delhi"
                + "\n"
                + "Best regards,\n"
                + "BloodBank Admin\n"
                + "arbloodbank.1201@gmail.com";

        try {

            // pre-defined functionality
            msg.setFrom(new InternetAddress(USERNAME));

            InternetAddress[] toAddresses = {new InternetAddress(targetEmail)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject("Blood Request Successful!");
            msg.setText(body);

            // sends the e-mail
            Transport.send(msg);

            return true;
        } catch (MessagingException m) {
            System.out.println("Exception occured in sending mail : " + m.getMessage());
        }
        return false;
    }

}
