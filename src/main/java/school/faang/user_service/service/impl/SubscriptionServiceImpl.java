package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.PaginationService;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final UserFilterService userFilterService;
    private final PaginationService paginationService;
    private final SubscriptionServiceValidator subscriptionServiceValidator;

    @Override
    public void followUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowIds(followerId, followeeId);
        subscriptionServiceValidator.checkIfAlreadySubscribed(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowIds(followerId, followeeId);
        subscriptionServiceValidator.checkIfAlreadySubscribed(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .collect(Collectors.toList());
        List<User> paginatedUsers = paginationService.applyPagination(followers, filter.getPage(), filter.getPageSize());
        List<User> filteredUsers = userFilterService.filterUsers(paginatedUsers, filter);

        return userMapper.toListUserDto(filteredUsers);
    }

    @Override
    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> following = subscriptionRepository.findByFollowerId(followerId)
                .collect(Collectors.toList());
        List<User> paginatedUsers = paginationService.applyPagination(following, filter.getPage(), filter.getPageSize());
        List<User> filteredUsers = userFilterService.filterUsers(paginatedUsers, filter);

        return userMapper.toListUserDto(filteredUsers);
    }

    @Override
    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
