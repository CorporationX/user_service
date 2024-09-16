package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CountDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.filter.UserFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final List<UserFilter> userFilters;

    private final UserMapper userMapper;

    @SneakyThrows
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Validation exception");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @SneakyThrows
    public void unFollowUser(long followerId, long followeeId) {
        if (followerId != followeeId) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("You can't unfollow yourself.");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .reduce(users,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .map(userMapper::toUserDto)
                .toList();
    }

    public CountDto getFollowersCount(long followeeId) {
        return userMapper.toCountDto(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId));
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> following = subscriptionRepository.findByFollowerId(followerId);
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .reduce(following,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .map(userMapper::toUserDto)
                .toList();
    }

    public CountDto getFollowingCount(long followerId) {
        return userMapper.toCountDto(subscriptionRepository.findFolloweesAmountByFollowerId(followerId));
    }
}
