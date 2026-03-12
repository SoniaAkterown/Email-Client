package com.example.emailwrapper.controller;

import com.example.emailwrapper.model.EmailRequest;
import com.example.emailwrapper.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox() {
        try {
            java.util.List<com.example.emailwrapper.model.EmailDTO> emails = emailService.receiveEmails();
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch emails: " + e.getMessage()));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getSentEmails() {
        return ResponseEntity.ok(emailService.getSentEmails());
    }
}
