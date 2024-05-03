package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.mockito.Mockito.verify;
import static school.faang.user_service.util.TestUser.FOLLOWEE_ID;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Test
    public void testFollowUserToAnotherUser() {
        subscriptionRepository.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserToAnotherUser() {
        subscriptionRepository.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowersCount() {
        subscriptionRepository.findFollowersAmountByFolloweeId(FOLLOWEE_ID);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(FOLLOWEE_ID);
    }
}