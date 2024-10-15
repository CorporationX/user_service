package school.faang.user_service.service.user;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.application.UserIdsReceivedEvent;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserBanService {
    private final UserRepository userRepository;

    @EventListener
    public synchronized void processUserUpdates(UserIdsReceivedEvent event) {
        retryUpdateUsers(event.getUserIds());
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void retryUpdateUsers(List<Long> ids) {
        updateUsers(ids);
    }

    @Transactional
    public void updateUsers(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            return;
        }
        for (User user : users) {
            user.setBanned(true);
        }
        userRepository.saveAll(users);
    }
}
