package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("follower is already subscribed to this followee");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("user {} followed to user {}", followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("user cannot unsubscribe from this user - the user is not its follower");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("user {} unfollowed from user {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        int followersCount = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(followersCount);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        int followeesCount = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new CountResponse(followeesCount);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto userFiltersDto) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return applyFiltersAndMapToDto(followers, userFiltersDto);
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto userFiltersDto) {
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return applyFiltersAndMapToDto(followees, userFiltersDto);
    }

    private List<UserDto> applyFiltersAndMapToDto(Stream<User> userStream, UserFiltersDto userFiltersDto) {
        String namePattern = userFiltersDto.namePattern().toLowerCase();
        String phonePattern = userFiltersDto.phoneNumber();
        int experienceMin = userFiltersDto.experienceMin();
        int experienceMax = userFiltersDto.experienceMax();

        return userStream.filter(user -> user.getUsername().toLowerCase().contains(namePattern))
                .filter(user -> user.getPhone().contains(phonePattern))
                .filter(user -> user.getExperience() >= experienceMin
                        && user.getExperience() <= experienceMax)
                .map(userMapper::toUserDto)
                .toList();
    }
}
