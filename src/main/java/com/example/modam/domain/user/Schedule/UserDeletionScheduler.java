package com.example.modam.domain.user.Schedule;

import com.example.modam.domain.user.Domain.UserStatus;
import com.example.modam.domain.user.Interface.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

    private final UserRepository userRepository;

    private static final long WITHDRAWL_GRACE_DAYS = 14;

    @Scheduled(cron= "0 0 0 * * *")
    @Transactional
    public void performPhysicalDeletion(){
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(WITHDRAWL_GRACE_DAYS);

        userRepository.findAllByStatusAndWithdrawalRequestedAtBefore(
                UserStatus.WITHDRAWAL_PENDING,
                cutoffTime
        ).forEach(user -> {
            System.out.println("Physically deleting user: " + user.getId() +
                    ", required at: " + user.getWithdrawalRequestedAt());

            userRepository.delete(user);
        });
    }
}
