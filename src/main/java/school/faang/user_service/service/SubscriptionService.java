package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserFilter userFilter;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> userStreamFromRepository = subscriptionRepository.findByFolloweeId(followeeId);
        List<User> userListFromRepository = userFilter.filterUsers(userStreamFromRepository, filter);
        return subscriptionMapper.toListUserDto(userListFromRepository);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        Stream<User> userStreamFromRepository = subscriptionRepository.findByFollowerId(followerId);
        List<User> userListFromRepository = userFilter.filterUsers(userStreamFromRepository, filter);
        return subscriptionMapper.toListUserDto(userListFromRepository);
    }
}
