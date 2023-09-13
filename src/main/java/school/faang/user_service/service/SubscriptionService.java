package school.faang.user_service.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Builder
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final FollowerEventPublisher followerEventPublisher;

    public void followUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("This subscription already exists");
        }
        followerEventPublisher.publish(FollowerEventDto
                .builder()
                .followerId(followerId)
                .foloweeId(followeeId)
                .build());
    }

    public void unfollowUser(long followerId, long followeeId){
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("You are not subscribed to unfollow");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters){
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        userFilters.stream().filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(followers, filters));
        return followers.stream().map(userMapper::toDto).toList();
    }

    public int getFollowersCount(long followeeId){
        if (subscriptionRepository.existsById(followeeId)) {
            return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        } else {
            throw new DataValidationException("User with this Id does not exist");
        }
    }

    public int getFolloweesCount(long followerId){
        if (subscriptionRepository.existsById(followerId)) {
            return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        } else {
            throw new DataValidationException("User with this Id does not exist");
        }
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters){
        List<User> users = subscriptionRepository.findByFollowerId(followerId).toList();
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));
        return users.stream().map(userMapper::toDto).toList();
    }
}