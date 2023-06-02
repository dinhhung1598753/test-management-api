package com.demo.app.event;

import com.demo.app.model.Token;
import com.demo.app.model.User;
import com.demo.app.repository.TokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisterCompleteEventListener implements ApplicationListener<RegisterCompleteEvent> {

    private final JavaMailSender mailSender;

    private final TokenRepository tokenRepository;

    private User user;

    @Override
    public void onApplicationEvent(RegisterCompleteEvent event) {
        user = event.getUser();
        String verifyToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token(verifyToken)
                .type(Token.TokenType.OTP)
                .user(user)
                .build();
        tokenRepository.save(token);
        String url = event.getUrl() + "/verify-email?token=" + verifyToken;
        try{
            sendVerifyEmail(url);
        } catch (MessagingException | UnsupportedEncodingException ex){
            throw new RuntimeException(ex);
        }
    }

    private void sendVerifyEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p>Thank you for registering with us,"+
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        var message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("knkuro00@gmail.com", "Verification Code Generator");
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject("Email Verification");
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
