package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters = new ArrayList<>();

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Already followed");
        }
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("You are not following this user");
        }
    }

    @Transactional
    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);
        Stream<User> filteredStream = filter(userStream, filters);
        return filteredStream.toList();
    }

    @Transactional
    public List<User> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followerId);
        Stream<User> filteredStream = filter(userStream, filters);
        return filteredStream.toList();
    }

    public Stream<User> filter(Stream<User> users, UserFilterDto filters) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters);
            }
        }
        return users;
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
