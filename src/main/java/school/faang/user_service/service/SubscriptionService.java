package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.exception.EntityStateException;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.filter.subfilter.SubscriberFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserMapper mapper;
    private final List<UserFilter> filters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new EntityStateException("User has already followed");
        }
        repository.followUser(followerId, followeeId);
    }
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (!repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new EntityStateException("User wasn't following");
        }
        repository.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        var users = repository.findByFollowerId(followeeId);
        var filteredUsers = filterUsers(users, filter);
        checkExistence(filteredUsers, "followers");
        return filteredUsers;
    }

    @Transactional
    public long getFollowersCount(long followeeId) {
        return repository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        var users = repository.findByFolloweeId(followeeId);
        var filteredUsers = filterUsers(users, filter);
        checkExistence(filteredUsers, "followees");
        return filteredUsers;
    }
    @Transactional
    public int getFollowingCount(long followerId) {
        return repository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        return filters.stream()
                .filter(subFilter -> subFilter.isApplicable(filter))
                .reduce(users, (stream, subFilter) -> subFilter.apply(stream, filter), Stream::concat)
                .map(mapper::toDto)
                .skip(filter.getPage() > 0 ? (long) (filter.getPage() - 1) * filter.getPageSize() : 0)
                .limit(filter.getPage() > 0 && filter.getPageSize() > 0 ? filter.getPageSize() : Long.MAX_VALUE)
                .toList();
    }

    private void checkExistence(List<UserDto> users, String exceptionMsg) {
        if (users.isEmpty()) {
            throw new UserNotFoundException(String.format("No %s with current filters", exceptionMsg));
        }
    }
}
