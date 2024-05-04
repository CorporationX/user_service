package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.TestUser.FOLLOWEE_ID;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;
    @Mock
    UserContext userContext;
    @Mock
    SubscriptionValidator subscriptionValidator;
    @InjectMocks
    private SubscriptionController subscriptionController;


    @Test
    public void testUnsubscribeUserFromAnotherUser() {
        when(userContext.getUserId()).thenReturn(1L);
        subscriptionController.unfollowUser(FOLLOWEE_ID);
        verify(subscriptionService, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionValidator, times(1)).validateUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testSubscribeUserToAnotherUser() {
        when(userContext.getUserId()).thenReturn(1L);
        subscriptionController.followUser(FOLLOWEE_ID);
        verify(subscriptionService, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionValidator, times(1)).validateUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowerCount() {
        when(userContext.getUserId()).thenReturn(1L);
        subscriptionController.getFollowersCount();
        verify(subscriptionService, times(1)).getFollowersCount(FOLLOWER_ID);
    }

}