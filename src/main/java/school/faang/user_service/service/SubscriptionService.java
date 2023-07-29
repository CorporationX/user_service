package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (isNotValid(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with id %d already follow user with id %d", followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!isNotValid(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with id %d doesn't follow user with id %d", followerId, followeeId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter){
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    private boolean isNotValid(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
        if (filters == null) {
            return users.map(userMapper::toDto).toList();
        }
        return userFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .flatMap(filter -> filter.apply(users, filters))
                    .map(userMapper::toDto)
                    .toList();
    }
}

