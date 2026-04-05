package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    public final SubscriptionRepository subscriptionRepository;
    public final UserService userService;
    public final UserContext userContext;

    @Override
    public void followUser(long followeeId) {
        long followerId = userContext.getUserId();
        log.info("User {} started following user {}", followerId, followeeId);
        if(followerId == followeeId){
            throw new DataValidationException("Нельзя подписаться на самого себя!");
        }
        userService.findById(followerId);
        userService.findById(followeeId);
        subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Метод \"followUser\" успешно применён: followerId={}, followeeId={}", followerId, followeeId);
    }
}