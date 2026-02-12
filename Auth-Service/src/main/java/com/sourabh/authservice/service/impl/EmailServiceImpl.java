package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.service.contract.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText(
                "Your OTP: " + otp + "\n\n" +
                        "Valid for 5 minutes.\n" +
                        "Do not share this code."
        );

        mailSender.send(message);
    }
}
