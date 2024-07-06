package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("Method followUser get exception");
            throw new DataValidationException("You are already subscribed to this user");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    @Transactional
    public void unFollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        } else {
            log.error("Method unFollowUser get exception");
            throw new DataValidationException("Subscription not found");
        }
    }

    @Transactional
    public List<UserDto> getFollowers(long followerId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followerId);
        return getUserDtos(filters, followers);
    }

    @Transactional
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        Stream<User> followee = subscriptionRepository.findByFolloweeId(followeeId);
        return getUserDtos(filters, followee);
    }

    @NotNull
    private List<UserDto> getUserDtos(UserFilterDto filters, Stream<User> followee) {
        List<UserFilter> userFiltersList = userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .toList();
        List<User> finalUserList = followee.toList();
        for (UserFilter userFilter : userFiltersList) {
            finalUserList = userFilter.apply(finalUserList.stream(), filters).toList();
        }
        return finalUserList.stream()
                .map(userMapper::toDTO).toList();
    }


    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }


    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFollowersAmountByFollowerId(followerId);
    }


}
