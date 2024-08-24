package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.FollowerMessagePublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;


    private List<UserFilter> userFilter;

    @Mock
    private UserFilter nameFilter;

    @Mock
    private UserFilter cityFilter;

    @Mock
    private FollowerMessagePublisher followerMessagePublisher;

    private long follower;
    private long followee;
    private UserFilterDto filterDto;
    private UserDto userDtoFirst;
    private UserDto userDtoSecond;
    private Stream<User> userStream;
    private User userFirst;
    private User userSecond;

    @BeforeEach
    void setup() {
        userDtoFirst = new UserDto();
        userDtoSecond = new UserDto();
        userFirst = new User();
        userSecond = new User();
        filterDto = new UserFilterDto();
        follower = 1;
        followee = 2;
        userStream = Stream.of(userFirst, userSecond);
        userFilter = List.of(nameFilter, cityFilter);
        subscriptionService = new SubscriptionService(subscriptionRepository,
                userMapper, userFilter, followerMessagePublisher);
    }


    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    void testFollowerUserWhenExistTrue() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower, followee)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.follow(follower, followee));
    }

    @Test
    void testFollowerUserWhenExistFalse() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower, followee)).thenReturn(false);
        subscriptionService.follow(follower, followee);

        verify(subscriptionRepository, atLeastOnce()).followUser(follower, followee);
    }

    @Test
    void testUnFollowerExistTrue() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower, followee)).thenReturn(true);
        subscriptionService.unfollow(follower, followee);

        verify(subscriptionRepository, atLeastOnce()).unfollowUser(follower, followee);

    }

    @Test
    void testUnFollowerUserWhenExistFalse() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(follower, followee)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollow(follower, followee));
    }

    @Test
    void testGetFollowersFindByFollowerId() {
        when(subscriptionRepository.findByFollowerId(follower)).thenReturn(userStream);
        when(userMapper.toDto(userFirst)).thenReturn(userDtoFirst);
        when(userMapper.toDto(userSecond)).thenReturn(userDtoSecond);
        when(userFilter.get(0).isApplicable(any())).thenReturn(true);
        when(userFilter.get(0).apply(any(), any())).thenReturn(Stream.of(userFirst, userSecond));

        subscriptionService.getFollowers(follower, filterDto);

        verify(subscriptionRepository, atLeastOnce()).findByFollowerId(follower);
        verify(userMapper, atLeastOnce()).toDto(userFirst);
        verify(userMapper, atLeastOnce()).toDto(userSecond);
        verify(userFilter.get(0), times(1)).isApplicable(any());
        verify(userFilter.get(0), times(1)).apply(any(), any());

    }


    @Test
    void testGetFollowingFindByFolloweeId() {
        when(subscriptionRepository.findByFolloweeId(followee)).thenReturn(userStream);
        when(userMapper.toDto(userFirst)).thenReturn(userDtoFirst);
        when(userMapper.toDto(userSecond)).thenReturn(userDtoSecond);
        when(userFilter.get(1).isApplicable(any())).thenReturn(true);
        when(userFilter.get(1).apply(any(), any())).thenReturn(Stream.of(userFirst));

        subscriptionService.getFollowing(followee, filterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followee);
        verify(userMapper, atLeastOnce()).toDto(userFirst);
        verify(userMapper, atLeastOnce()).toDto(userSecond);
        verify(userFilter.get(1), times(1)).isApplicable(any());
        verify(userFilter.get(1), times(1)).apply(any(), any());
    }


    @Test
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(follower);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(follower);
    }

    @Test
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(follower);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFollowerId(follower);
    }

}