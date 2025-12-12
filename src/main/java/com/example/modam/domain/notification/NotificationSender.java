package com.example.modam.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationSender {

    private Set<String> fcmSet = new HashSet<>();

    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 1 * ?")
    public void reportNotification() {
        for (String fcm : fcmSet) {
            String title = "모담 리포트 도착!";
            String body = "이번 달 독서 리포트를 확인해보세요!";
            notificationService.sendNotification(title, body, fcm);
        }
    }
}
