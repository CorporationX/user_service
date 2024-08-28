package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final FollowerEventPublisher publisher;

    @Transactional
    public void followUser(long followerId, long followeeId) throws DataFormatException {
        validateUsersSubs(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        publisher.publish(new FollowerEvent(followerId, followeeId));
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        validateUsersSubs(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public void validateUsersSubs(long followerId, long followeeId) throws DataFormatException {
        if (followerId == followeeId) {
            throw new DataFormatException("Same id users");
        }

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException("There are no users with this id");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) throws DataFormatException {
        validateUser(followeeId);
        Stream<User> followees = subscriptionRepository.findByFolloweeId(followeeId);

        return filterUsers(filter, followees);

    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) throws DataFormatException {
        validateUser(followerId);
        Stream<User> followers = subscriptionRepository.findByFollowerId(followerId);

        return filterUsers(filter, followers);
    }

    public int getFollowersCount(long followeeId) throws DataFormatException {
        validateUser(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) throws DataFormatException {
        validateUser(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    public void validateUser(long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsById(followeeId)) {
            throw new DataFormatException("there are no user with this id");
        }
    }

    public List<UserDto> filterUsers(UserFilterDto filters, Stream<User> users) throws DataFormatException {
        if (users == null) {
            throw new NullPointerException("empty followers");
        }

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(users, filters))
                .map(userMapper::toDto)
                .toList();
    }
}