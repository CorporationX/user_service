package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserContext userContext;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {

        try {
            if (!validateId(followerId) && !findIdInSubscribers(followerId)) {
                subscriptionRepository.followUser(followerId, followeeId);
            }
        } catch (ForbiddenException forbiddenException) {
            forbiddenException.getMessage();
        }
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) throws ForbiddenException {
        if (findIdInSubscribers(followerId)) {
            try {
                subscriptionRepository.unfollowUser(followerId, followeeId);
            } catch (ForbiddenException f) {
                f.getMessage();
            }
        } else {
            log.info("You are not subscriber " + followerId);
        }
    }

    @Override
    @Transactional
    public CountResponse getFollowersCount(long followeeId) {
        return new CountResponse(subscriptionRepository.findByFollowerId(followeeId).count());
    }

    @Override
    @Transactional
    public CountResponse getFolloweesCount(long followerId) {
        return new CountResponse(subscriptionRepository.findByFollowerId(followerId).count());
    }

    @Override
    @Transactional
    public List<UserDto> getFollowers(long followeeId) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followeeId);
        return users.map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    @Transactional
    public List<UserDto> getFollowees(long followerId) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followerId);
        return users.map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    public boolean validateId(long id) {

        return userContext.getUserId() == id;
    }

    @Override
    public boolean findIdInSubscribers(long id) {
        return subscriptionRepository.findByFollowerId(id).anyMatch(user -> user.getId()
                .equals(userContext.getUserId()));
    }
}
