package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserIdsSubscriberValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserIdsSubscriber {
    private final UserRepository userRepository;
    private final UserIdsSubscriberValidator userIdsSubscriberValidator;


    public void handleMessage(Object message) {
        List<Long> userIds = userIdsSubscriberValidator.parseUserIds(message);

        log.info("Received message to ban users: {}", userIds);

        for (Long userId : userIds) {
            log.info("Received message to ban user: {}", userId);
            userIdsSubscriberValidator.validateId(userId);

            User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));
            user.setBanned(true);
            userRepository.save(user);
        }
    }
}
