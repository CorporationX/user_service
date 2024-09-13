package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.constant.ErrorMessages;
import school.faang.user_service.constant.SubscriptionConst;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserFilter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;

    @Transactional
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

    @Transactional
    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);

        Stream<User> filteredUsers = filterUsers(users, filters);
        Stream<User> paginatedUsers = applyPagination(filteredUsers, filters);

        return paginatedUsers.toList();
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public List<User> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);

        Stream<User> filteredUsers = filterUsers(users, filters);
        Stream<User> paginatedUsers = applyPagination(filteredUsers, filters);

        return paginatedUsers.toList();
    }

    @Transactional
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        Predicate<User> userFilterPredicate = userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .map(filter -> filter.getPredicate(filters))
                .reduce(user -> true, Predicate::and);
        return users.filter(userFilterPredicate);
    }

    private Stream<User> applyPagination(Stream<User> users, UserFilterDto filters) {
        return users
                .skip(filters.getPage() * filters.getPageSize())
                .limit(filters.getPageSize() == 0 ? SubscriptionConst.DEFAULT_PAGE_SIZE : filters.getPageSize());
    }

    private void checkNotToFollowOrUnfollowToSelf(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(ErrorMessages.CANNOT_SUBSCRIBE_OR_UNSUBSCRIBE_TO_SELF);
        }
    }
}
