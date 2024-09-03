package school.faang.user_service.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.filters.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validation.UserValidation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Spy
    private UserMapper userMapper;
    @Spy
    private List<UserFilter> userFilters;
    @Mock
    private UserValidation userValidation;

    private final long NEGATIVE_VALUE = -1L;
    private final long POSITIVE_ONE = 1L;
    private final long POSITIVE_TWO = 2L;

    private final int ONE = 1;

    @Test
    void testFollowUserWithEqualsId() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_ONE;

        assertThrows(ValidationException.class,
                () -> subscriptionService.followUser(followId, followeeId),
                "User can't subscribe to himself");
    }

    @Test
    void testFollowUserIfSubscriptionExists() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_TWO;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)).thenReturn(Boolean.TRUE);

        assertThrows(ValidationException.class,
                () -> subscriptionService.followUser(followId, followeeId),
                "Already subscribed");
    }

    @Test
    void testFollowUser() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_TWO;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)).thenReturn(Boolean.FALSE);

        subscriptionService.followUser(followId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followId, followeeId);
    }

    @Test
    void testUnfollowUserWithNegativeId() {
        long followId = NEGATIVE_VALUE;
        long followeeId = NEGATIVE_VALUE;

        assertThrows(ValidationException.class,
                () -> subscriptionService.unfollowUser(followId, followeeId),
                "User id can't be less 0");
    }

    @Test
    void testUnfollowUserWithNotExistedUser() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_TWO;

        assertThrows(ValidationException.class,
                () -> subscriptionService.unfollowUser(followId, followeeId));
    }

    @Test
    void testUnfollowUserWithEqualsId() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_ONE;

        assertThrows(ValidationException.class,
                () -> subscriptionService.unfollowUser(followId, followeeId),
                "User can't unsubscribe to himself");
    }

    @Test
    void testUnfollowUserIfSubscriptionNotExists() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_TWO;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)).thenReturn(Boolean.FALSE);

        assertThrows(ValidationException.class,
                () -> subscriptionService.unfollowUser(followId, followeeId),
                "Already unsubscribed");
    }


    @Test
    void testUnfollowUser() {
        long followId = POSITIVE_ONE;
        long followeeId = POSITIVE_TWO;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)).thenReturn(Boolean.TRUE);

        subscriptionService.unfollowUser(followId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followId, followeeId);
    }

    @Test
    void testGetFollowersCount() {
        long followeeId = POSITIVE_TWO;

        subscriptionService.getFollowersCount(followeeId);

        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void testGetFollowingCount() {
        long followId = POSITIVE_ONE;

        subscriptionService.getFollowingCount(followId);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followId);
    }

    @Test
    void testGetFollowersWithNullFilter() {
        long followeeId = POSITIVE_TWO;
        UserFilterDto userFilterDto = null;

        subscriptionService.getFollowing(followeeId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    //@Test
    //TODO
    void testGetFollowersWithFilter() {
        long followeeId = POSITIVE_TWO;

        UserNameFilter nameFilter = new UserNameFilter();
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("es");

        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        User testUser = new User();
        testUser.setId(2L);
        testUser.setUsername("test");

        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherTest");

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user, testUser, anotherUser));
        when(userFilters.stream()).thenReturn(Stream.of(nameFilter));

        List<UserDto> result = subscriptionService.getFollowing(followeeId, userFilterDto);

        assertEquals(ONE, result.size());

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    void testGetFollowingWithNullFilter() {
        long followerId = POSITIVE_TWO;
        UserFilterDto userFilterDto = null;

        subscriptionService.getFollowing(followerId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followerId);
    }

    //TODO @Test
    void testGetFollowingWithFilter() {
    }
}