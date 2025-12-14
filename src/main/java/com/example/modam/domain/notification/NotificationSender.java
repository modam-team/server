package com.example.modam.domain.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationSender {

    private Set<String> fcmSet = new HashSet<>();

    private final NotificationService notificationService;

    /*알림 QA용 API*/
    @PostMapping("/notify")
    public void notify(@RequestParam String fcm) {
        String title = "모담 리포트 도착!";
        String body = "이번 달 독서 리포트를 확인해보세요!";
        notificationService.sendNotification(title, body, fcm);
    }

    /*알림 QA시 FCM 토큰 전송용 API*/
    @PostMapping("/log-token")
    public void logToken(@RequestBody String token) {
        log.info(" FCM TOKEN RECEIVED: {}", token);
    }

    @Scheduled(cron = "0 0 12 1 * ?")
    public void reportNotification() {
        for (String fcm : fcmSet) {
            String title = "모담 리포트 도착!";
            String body = "이번 달 독서 리포트를 확인해보세요!";
            notificationService.sendNotification(title, body, fcm);
        }
    }
}
