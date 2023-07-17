package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.user_filters.UserFilter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).collect(Collectors.toList());
        userFilters.stream().filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(followers, filters));
        return followers.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    private void validate(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
    }
}