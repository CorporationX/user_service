package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private Long userId1 = 1000L;
    private Long userId2 = 2000L;

    @Mock
    private SubscriptionRepository subscriptionRepository;

//    @Spy
//    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    public void testFollowUserThrowsExceptionWhenFollowsItself() {
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId1));
    }

    @Test
    public void testFollowUserThrowsExceptionWhenSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(userId1, userId2)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId2));
    }

    @Test
    public void testFollowUser() {
        subscriptionService.followUser(userId1, userId2);
        verify(subscriptionRepository, times(1)).followUser(userId1, userId2);
    }

    @Test
    public void testUnfollowUserThrowsExceptionWhenUnfollowYourself() {
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(userId2, userId2));
    }
    @Test
    public void testUnfollowUser() {
        subscriptionService.unfollowUser(userId1, userId2);
        verify(subscriptionRepository, times(1)).unfollowUser(userId1, userId2);
    }

    @Test
    public void testGetFollowers() {
//        User user1 = new User();
//        user1.setId(userId1);
//        user1.setFollowers();
//        when(subscriptionRepository.findByFollowerId(userId1)).thenReturn(user1.getFollowers().stream());
//

    }

    @Test
    public void testGetFollowersCount() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(userId1)).thenReturn(100);
        subscriptionService.getFollowersCount(userId1);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(userId1);
        assertEquals(100, subscriptionService.getFollowersCount(userId1));
    }

    @Test
    public void testGetFollowing() {

    }

    @Test
    public void testGetFollowingCount() {
        subscriptionService.getFollowingCount(userId1);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(userId1)).thenReturn(500);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(userId1);
        assertEquals(500, subscriptionService.getFollowingCount(userId1));
    }
}
