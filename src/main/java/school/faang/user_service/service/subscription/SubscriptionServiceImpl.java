package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filters.SubscriptionUserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserFilter userFilter;
    private final UserMapper userMapper;
    private final SubscriptionValidator validator;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        validator.checkSubscriptionOnHimself(followerId, followeeId);
        validator.checkIfSubscriptionExists(followerId, followeeId);
        validator.validateUsers(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        validator.checkIfSubscriptionNotExists(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto filters) {
        validator.validateUser(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return userMapper.toSubscriptionUserDtos(userFilter.filterUsers(followers, filters));
    }

    @Override
    public int getFollowersCount(Long followeeId) {
        validator.validateUser(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowings(Long followerId, UserFilterDto filters) {
        validator.validateUser(followerId);
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return userMapper.toSubscriptionUserDtos(userFilter.filterUsers(followees, filters));
    }

    @Override
    public int getFollowingCounts(Long followerId) {
        validator.validateUser(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
