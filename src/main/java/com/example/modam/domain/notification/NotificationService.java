package com.example.modam.domain.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title, String body, String fcmToken) {
        send(createMessage(title, body, fcmToken));
    }

    private void send(Message message) {
        try {
            String response = firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send Notification : {}", e.getMessage());
        }
    }

    // 앱 알림 -> data만 보냄
    private Message createMessage(String title, String body, String fcmToken) {
        return Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(fcmToken)
                .build();
    }

    // 웹 알림
    /*
    private Message createMessage(String title, String body, String fcmToken) {
        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .setToken(fcmToken)
                .build();
    }*/
}
