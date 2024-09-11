package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final UserFilterService userFilterService;
    private final PaginationService paginationService;
    private final SubscriptionServiceValidator subscriptionServiceValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowIds(followerId, followeeId);
        subscriptionServiceValidator.checkIfAlreadySubscribed(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowIds(followerId, followeeId);
        subscriptionServiceValidator.checkIfAlreadySubscribed(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .collect(Collectors.toList());
        List<User> paginatedUsers = paginationService.applyPagination(followers, filter.getPage(), filter.getPageSize());
        List<User> filteredUsers = userFilterService.filterUsers(paginatedUsers, filter);

        return userMapper.toListUserDto(filteredUsers);
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> following = subscriptionRepository.findByFollowerId(followerId)
                .collect(Collectors.toList());
        List<User> paginatedUsers = paginationService.applyPagination(following, filter.getPage(), filter.getPageSize());
        List<User> filteredUsers = userFilterService.filterUsers(paginatedUsers, filter);

        return userMapper.toListUserDto(filteredUsers);
    }

    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
