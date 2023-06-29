package com.demo.app.data;

import com.demo.app.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AppScheduler {

    private final TokenRepository tokenRepository;

    @Transactional
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void revokeToken(){
        tokenRepository.deleteAllByRevokedTrue();
    }

}
