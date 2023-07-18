package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.SubscriberFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserMapper mapper;
    private final SubscriberFilter subFilter;

    public void followUser(long followerId, long followeeId) {
        if (repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User has already followed");
        }
        repository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        repository.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        var users = repository.findByFolloweeId(followeeId)
                .filter(user -> subFilter.matchesFilters(user, filter))
                .map(mapper::toDto)
                .toList();
        return users;
    }
}
