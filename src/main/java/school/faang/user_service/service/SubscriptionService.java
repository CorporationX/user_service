package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        validateUserId(followeeId);
        Stream<User> allUsers = subscriptionRepository.findByFolloweeId(followeeId);
        return userFilters == null ? userMapper.toDtoList(allUsers.collect(Collectors.toList())) : filterUsers(allUsers, filter);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        Stream<User> filteredUsers = users;
        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                filteredUsers = userFilter.apply(filteredUsers, filter);
            }
        }
        return userMapper.toDtoList(filteredUsers.toList());
    }

    private void validateUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id cannot be negative");
        }
    }
}