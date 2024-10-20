package school.faang.user_service.service.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.follow.FollowEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> filters;
    private final ObjectMapper objectMapper;
    private final FollowEventPublisher followEventPublisher;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует");
        }
        subscriptionRepository.followUser(followerId, followeeId);

        FollowEvent followEvent = new FollowEvent();
        followEvent.setFollowerId(followerId);
        followEvent.setFolloweeId(followeeId);
        followEvent.setFollowedAt(LocalDateTime.now());

        try {
            String json = objectMapper.writeValueAsString(followEvent);
            followEventPublisher.publish(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<UserDto> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto)
                .toList();

        return filterUsers(followers, filter);
    }

    @Override
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        List<UserDto> following = subscriptionRepository.findByFollowerId(followeeId)
                .map(userMapper::toDto)
                .toList();

        return filterUsers(following, filter);
    }

    @Override
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filters.stream().allMatch(f -> f.apply(user, filter)))
                .toList();
    }
}


