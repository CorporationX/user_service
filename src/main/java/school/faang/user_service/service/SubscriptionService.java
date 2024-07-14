package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Stream;

@Service
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;
    private UserMapper mapper;
    private List<UserFilter> userFilters;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserMapper mapper, List<UserFilter> userFilters) {
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
        this.userFilters = userFilters;
    }

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует");
        }
        subscriptionRepository.followUser(followerId, followeeId);
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
