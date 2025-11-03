package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.service.user.validator.SubscriptionSystemValidator;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserSubscriptionService {
    private final SubscriptionSystemValidator subscriptionSystemValidator;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;


    public void followUser(long followerId, long followeeId) {
        subscriptionSystemValidator.followValidation(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Now user {} is following {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionSystemValidator.unfollowValidation(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User {} doesn't follow {} anymore", followerId, followeeId);
    }

    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(count);
    }

    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new CountResponse(count);
    }

    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        List<UserDto> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toUserDto)
                .filter(userDto -> applyFilters(userDto, filters))
                .toList();
        log.info("User {} followers: {}", followeeId, followers.stream().map(UserDto::id).toList());
        return followers;
    }

    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        List<UserDto> followees = subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toUserDto)
                .filter(userDto -> applyFilters(userDto, filters))
                .toList();
        log.info("User {} followees: {}", followerId, followees.stream().map(UserDto::id).toList());
        return followees;
    }

    private boolean applyFilters(UserDto user, UserFiltersDto filters) {
        if (filters == null) {
            return true;
        }
        if (filters.getNamePattern() != null
                && !user.username().toLowerCase().contains(filters.getNamePattern().toLowerCase())) {
            return false;
        }
        return filters.getPhonePattern() == null
                || (user.phone() != null && user.phone().contains(filters.getPhonePattern()));
    }
}
