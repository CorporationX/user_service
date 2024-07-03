package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.userFilter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        validExistsByFollowerIdAndFolloweeId(followerId, followeeId);
        validExistsById(followerId);
        validExistsById(followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validExistsByFollowerIdAndFolloweeId(followerId, followeeId);
        validExistsById(followerId);
        validExistsById(followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }


    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        validExistsById(followeeId);
        validUserFilterDtoByNull(filterDto);

        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(followers, filterDto));
        return userMapper.toDto(followers.toList());
    }

    public Integer getFollowersCount(long followeeId) {
        validExistsById(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        validExistsById(followeeId);
        validUserFilterDtoByNull(filterDto);

        Stream<User> followings = subscriptionRepository.findByFolloweeId(followeeId);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(followings, filterDto));
        return userMapper.toDto(followings.toList());
    }

    public Integer getFollowingCount(long followerId) {
        validExistsById(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validExistsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User id: " + followerId
                    + " already followed to the user id: " + followeeId);
        }
    }

    private void validExistsById(long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new DataValidationException("User " + id + " not found");
        }
    }

    private void validUserFilterDtoByNull(UserFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("UserFilterDto cannot be null");
        }
    }
}
