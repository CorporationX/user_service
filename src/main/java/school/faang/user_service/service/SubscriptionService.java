package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.userFilter.UserFilter;
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

        followers = userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(followers, (currentFollowings, filter) ->
                        filter.apply(currentFollowings, filterDto), (a, b) -> b);

        return userMapper.toDto(followers);
    }

    public Integer getFollowersCount(long followeeId) {
        subscriptionServiceValidator.validateExistsById(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        subscriptionServiceValidator.validateGetFollowing(followeeId, filterDto);

        List<User> followings = subscriptionRepository.findByFolloweeId(followeeId).toList();

        followings = userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(followings, (currentFollowings, filter) ->
                        filter.apply(currentFollowings, filterDto), (a, b) -> b);

        return userMapper.toDto(followings);
    }

    public Integer getFollowingCount(long followerId) {
        subscriptionServiceValidator.validateExistsById(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
