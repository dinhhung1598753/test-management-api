package com.demo.app.data;

import com.demo.app.repository.TokenRepository;
import com.demo.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AppScheduler {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;


    @Transactional
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.DAYS)
    public void cleanupUnverifiedAccounts(){
        var waitedTime = LocalDateTime.now().minusMinutes(5);
        var unverifiedUsers = userRepository.findByEnabledFalseAndCreatedAtBefore(waitedTime);
        unverifiedUsers.forEach(user -> {
            tokenRepository.deleteAllByUser(user.getId());
            user.setRoles(null);
            userRepository.save(user);
        });
        userRepository.deleteAll(unverifiedUsers);
    }

    @Transactional
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void revokeToken(){
        tokenRepository.deleteAllByRevokedTrue();
    }

}
