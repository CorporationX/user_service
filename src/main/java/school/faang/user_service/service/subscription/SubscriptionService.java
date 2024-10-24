package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.constant.ErrorMessages;
import school.faang.user_service.constant.SubscriptionConst;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.redis.publisher.subscribe.PublishSubscribeUserEvent;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserFilter;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;

    @Transactional
    @PublishSubscribeUserEvent
    public void followUser(long followerId, long followeeId) {
        checkNotToFollowOrUnfollowToSelf(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessages.ALREADY_SUBSCRIBE);
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        checkNotToFollowOrUnfollowToSelf(followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessages.USER_NOT_SUBSCRIBED);
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowers(long followeeId, UserExtendedFilterDto filters) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        List<User> filteredUsers = filterUsers(followers, filters);
        return applyPagination(filteredUsers, filters);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(long followerId, UserExtendedFilterDto filters) {
        List<User> following = subscriptionRepository.findByFollowerId(followerId);
        List<User> filteredUsers = filterUsers(following, filters);
        return applyPagination(filteredUsers, filters);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<User> filterUsers(List<User> users, UserExtendedFilterDto filters) {
        Predicate<User> userFilterPredicate = userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .map(filter -> filter.getPredicate(filters))
                .reduce(user -> true, Predicate::and);
        return users.stream().filter(userFilterPredicate).toList();
    }

    private List<User> applyPagination(List<User> users, UserExtendedFilterDto filters) {
        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .skip(filters.getPage() * filters.getPageSize())
                .limit(filters.getPageSize() == 0 ? SubscriptionConst.DEFAULT_PAGE_SIZE : filters.getPageSize())
                .toList();
    }

    private void checkNotToFollowOrUnfollowToSelf(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(ErrorMessages.CANNOT_SUBSCRIBE_OR_UNSUBSCRIBE_TO_SELF);
        }
    }
}
