package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        Stream<User> filteredUsers = subscriptionRepository.findByFollowerId(followerId);
        for (UserFilter filter : filters) {
            filteredUsers = filter.apply(filteredUsers, userFilterDto);
        }
        return filteredUsers
                .map(userMapper::toDto)
                .toList();
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
