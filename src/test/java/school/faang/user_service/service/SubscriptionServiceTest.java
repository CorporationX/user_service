package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Test
    public void testFollowUserToAnotherUser() {
        subscriptionRepository.followUser(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUserToAnotherUser() {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowersCount() {
        subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(FOLLOWER_ID);

        when(subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID))
                .thenReturn(1);
        int count = subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWER_ID);

        assertEquals(1, count);
    }
}