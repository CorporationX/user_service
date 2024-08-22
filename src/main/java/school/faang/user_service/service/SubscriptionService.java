package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final SubscriptionServiceValidator subscriptionServiceValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        subscriptionServiceValidator.validateGetFollowers(followeeId, filterDto);

        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return userMapper.toDtoList(userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(followers, filterDto))
                .toList());
    }

    public Integer getFollowersCount(long followeeId) {
        subscriptionServiceValidator.validateExistsById(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        subscriptionServiceValidator.validateGetFollowing(followeeId, filterDto);

        List<User> followings = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return userMapper.toDtoList(userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(followings, filterDto))
                .toList());
    }

    public Integer getFollowingCount(long followerId) {
        subscriptionServiceValidator.validateExistsById(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
