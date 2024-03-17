package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Component
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    @Qualifier("aboutPatternFilter")
    private final UserFilter aboutPatternFilter;
    @Qualifier("cityPatternFilter")
    private final UserFilter cityPatternFilter;
    @Qualifier("contactPatternFilter")
    private final UserFilter contactPatternFilter;
    @Qualifier("countryPatternFilter")
    private final UserFilter countryPatternFilter;
    @Qualifier("emailPatternFilter")
    private final UserFilter emailPatternFilter;
    @Qualifier("experienceMaxFilter")
    private final UserFilter experienceMaxFilter;
    @Qualifier("experienceMinFilter")
    private final UserFilter experienceMinFilter;
    @Qualifier("namePatternFilter")
    private final UserFilter namePatternFilter;
    @Qualifier("pageFilter")
    private final UserFilter pageFilter;
    @Qualifier("pageSizeFilter")
    private final UserFilter pageSizeFilter;
    @Qualifier("phonePatternFilter")
    private final UserFilter phonePatternFilter;
    @Qualifier("skillPatternFilter")
    private final UserFilter skillPatternFilter;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserMapper userMapper, List<UserFilter> userFilters) {
        this.subscriptionRepository = subscriptionRepository;
        this.userMapper = userMapper;
        this.userFilters = userFilters;
    }

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can not follow yourself!");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists!");
        }
        subscriptionRepository.followUser(followerId, followeeId);

    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can not unfollow yourself!");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);

    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));
        return userMapper.toDto(users.toList());
    }

}
