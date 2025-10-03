package school.faang.user_service.service.user;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserSubscriptionServiceImpl userSubscriptionService;

    @Test
    void testSungUpForYourself() {
        long userId = 1;
        assertThrows(ForbiddenException.class, () ->
                userSubscriptionService.followUser(userId, userId)
        );
    }

    @Test
    void alreadySignedTest() {
        long followerId = 1;
        long followeeId = 2;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);
        assertThrows(ForbiddenException.class, () ->
                userSubscriptionService.followUser(followerId, followeeId)
        );
    }

    @Test
    void followUserTest() {
        long followerId = 1;
        long followeeId = 2;

        userSubscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    void unsubscribeFromYourselfTest() {
        long userId = 1;
        assertThrows(ForbiddenException.class, () ->
                userSubscriptionService.followUser(userId, userId)
        );
    }

    @Test
    void andSoUnsignedTest() {
        long followerId = 1;
        long followeeId = 2;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);
        assertThrows(ForbiddenException.class, () ->
                userSubscriptionService.unfollowUser(followerId, followeeId)
        );
    }

    @Test
    void unfollowUserTest() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionRepository.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCountTest() {
        long userId = 1;

        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(userId))
                .thenReturn(15);
        CountResponse response = userSubscriptionService.getFollowersCount(userId);

        assertEquals(15, response.getCount());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(userId);
    }

    @Test
    void getFolloweesCount() {
        long userId = 1;

        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(userId))
                .thenReturn(15);
        CountResponse response = userSubscriptionService.getFolloweesCount(userId);

        assertEquals(15, response.getCount());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(userId);
    }

    @Test
    void getFollowersTest() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("Dima");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Nik");

        List<User> followers = List.of(user1, user2);
        long userId = 60;
        UserDto dto1 = new UserDto(1L, "Dima", "dimak@Mail", "77814132", "asd");
        UserDto dto2 = new UserDto(2L, "Nik", "Nik@Mail", "77814132", "asd");
        Mockito.when(subscriptionRepository.findByFolloweeId(userId))
                .thenReturn(followers.stream());
        Mockito.when(userMapper.toUserDto(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> expectedDtos = List.of(dto1, dto2);

        List<UserDto> result = userSubscriptionService.getFollowers(userId);
        assertEquals(expectedDtos, result);

        Mockito.verify(subscriptionRepository, Mockito.times(1)).findByFolloweeId(userId);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(user1);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(user2);
    }

    @Test
    void getFolloweesTest() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("Dima");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Nik");

        long userId = 60;
        UserDto dto1 = new UserDto(1L, "Dima", "dimak@Mail", "77814132", "asd");
        UserDto dto2 = new UserDto(2L, "Nik", "Nik@Mail", "77814132", "asd");
        List<User> followers = List.of(user1, user2);

        Mockito.when(subscriptionRepository.findByFollowerId(userId))
                .thenReturn(followers.stream());
        Mockito.when(userMapper.toUserDto(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> expectedDtos = List.of(dto1, dto2);

        List<UserDto> result = userSubscriptionService.getFollowees(userId);
        assertEquals(expectedDtos, result);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFollowerId(userId);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(user1);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(user2);
    }
}
