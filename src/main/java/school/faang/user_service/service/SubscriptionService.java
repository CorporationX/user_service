package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        boolean isExist = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isExist){
            throw new DataValidationException("Can`t subscribe to yourself");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId){
        validate(followerId, followeeId);
        boolean isExist = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isExist){
            throw new DataValidationException("Can`t unfollow of yourself");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters){
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));
        return userMapper.toDto(users.toList());
    }

    public void validate(Long firstId, Long secondId) {
        if (firstId <= 0 || secondId <= 0){
            throw new DataValidationException("Id cannot be less 0! ");
        } else if (firstId == null || secondId == null){
            throw new DataValidationException("Id cannot be null !");
        }
    }
}
