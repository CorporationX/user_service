package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.publisher.FollowerEventPublisher;
import school.faang.user_service.service.user.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper mapper;
    private final SubscriptionValidator validator;
    private final List<UserFilter<UserFilterDto, User>> userFilters;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validator.validateExistingSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        FollowerEvent followerEvent = FollowerEvent.builder().followerId(followerId).followeeId(followeeId).
        subscriptionTime(LocalDateTime.now()).build();
        followerEventPublisher.publish(followerEvent);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(filters, users);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followeeId);
        return filterUsers(filters, users);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(UserFilterDto filters, Stream<User> users) {
        List<UserDto> filteredUsers = userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(users, filters))
                .map(mapper::toDto)
                .toList();
        return new PageImpl<>(filteredUsers,
                PageRequest.of(filters.getPage() - 1, filters.getPageSize()),
                filteredUsers.size()).stream().toList();
    }
}
