package com.example.emailwrapper.service;

import com.example.emailwrapper.model.SentEmail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String EMAILS_FILE_PATH = "emails.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.imap.host}")
    private String imapHost;

    @Value("${spring.mail.imap.port}")
    private String imapPort;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        saveEmail(new SentEmail(to, subject, body,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    private void saveEmail(SentEmail email) {
        List<SentEmail> emails = getSentEmails();
        emails.add(email);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(EMAILS_FILE_PATH), emails);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SentEmail> getSentEmails() {
        File file = new File(EMAILS_FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<SentEmail>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<com.example.emailwrapper.model.EmailDTO> receiveEmails() {
        List<com.example.emailwrapper.model.EmailDTO> emails = new ArrayList<>();
        try {
            java.util.Properties props = new java.util.Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", imapHost);
            props.put("mail.imaps.port", imapPort);
            props.put("mail.imaps.ssl.enable", "true");

            jakarta.mail.Session session = jakarta.mail.Session.getInstance(props);
            jakarta.mail.Store store = session.getStore("imaps");
            store.connect(imapHost, username, password);

            jakarta.mail.Folder inbox = store.getFolder("INBOX");
            inbox.open(jakarta.mail.Folder.READ_ONLY);

            jakarta.mail.Message[] messages = inbox.getMessages();
            // Fetch last 20 emails
            int start = Math.max(0, messages.length - 20);
            for (int i = messages.length - 1; i >= start; i--) {
                jakarta.mail.Message message = messages[i];
                com.example.emailwrapper.model.EmailDTO emailDTO = new com.example.emailwrapper.model.EmailDTO();
                emailDTO.setId(String.valueOf(message.getMessageNumber())); // Use message number as ID for simplicity
                emailDTO.setSubject(message.getSubject());
                // Handle address potentially being null
                if (message.getFrom() != null && message.getFrom().length > 0) {
                    emailDTO.setFrom(message.getFrom()[0].toString());
                } else {
                    emailDTO.setFrom("Unknown");
                }
                emailDTO.setDate(message.getSentDate().toString());
                emailDTO.setBody(getTextFromMessage(message));
                emails.add(emailDTO);
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    private String getTextFromMessage(jakarta.mail.Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            jakarta.mail.internet.MimeMultipart mimeMultipart = (jakarta.mail.internet.MimeMultipart) message
                    .getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(jakarta.mail.internet.MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            jakarta.mail.BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof jakarta.mail.internet.MimeMultipart) {
                result.append(getTextFromMimeMultipart((jakarta.mail.internet.MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
}
