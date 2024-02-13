package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledUserService {
    private final static long MOTHS_TO_DELETE_USER = 3;
    private final UserRepository userRepository;

    @Scheduled(cron = "${my.schedule.cron}")
    public void deleteNonActiveUsers() {
        LocalDateTime timeToDelete = LocalDateTime.now().minusMonths(MOTHS_TO_DELETE_USER);
        userRepository.deleteAllInactiveUsersAndUpdatedAtOverMonths(timeToDelete);
    }
}
