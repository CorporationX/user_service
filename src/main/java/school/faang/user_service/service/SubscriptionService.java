package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ConflictException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.util.filter.Filter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<Filter<UserFilterDto, User>> userFilters;

    public void followUser(long followerId, long followeeId) {
        boolean followingExists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (followingExists) {
            throw new ConflictException(MessageError.FOLLOWING_EXISTS);
        }
        // нужно ли проверять существуют ли вообще такие пользователи? типо userRepository.existsById

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        boolean followingExists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!followingExists) {
            throw new ConflictException(MessageError.FOLLOWING_DOESNT_EXIST);
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followersStream = subscriptionRepository.findByFolloweeId(followeeId);
        List<User> filteredFollowers = filterUsers(followersStream, filter).toList();

        return userMapper.toDtoList(filteredFollowers);
    }

    private Stream<User> filterUsers(Stream<User> userStream, UserFilterDto filter) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                // по цепочке применяем фильтры к userStream
                .reduce(userStream,
                        (userStream1, userFilter) -> userFilter.apply(userStream1, filter),
                        ((userStream1, userStream2) -> userStream2))
                // полагаю что номер страницы приходит в пользовательком формате (начиная с 1)
                // default - 1 (валидация на уровне контроллера)
                .skip( (long) (filter.getPage() - 1) * filter.getPageSize())
                // default - 10
                .limit(filter.getPageSize());
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFolloweesCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
