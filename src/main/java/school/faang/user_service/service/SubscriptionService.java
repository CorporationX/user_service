package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) throws DataValidationException, DataFormatException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<UserDto> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());

        return filterUsers(followers, filter);
    }

    private List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filter.getNamePattern() == null || user.getUsername().matches(filter.getNamePattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().matches(filter.getEmailPattern()))
                .collect(Collectors.toList());
    }

    public int getFollowersCount(long followeeId) {
       return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        List<UserDto> following = subscriptionRepository.findByFollowerId(followeeId)
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());

        return filterUsers(following, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}


