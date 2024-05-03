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
    SubscriptionService service;
    @Mock
    UserContext ctx;
    @Mock
    SubscriptionValidator validator;
    @InjectMocks
    private SubscriptionController controller;

    @Test
    public void testSubscribeUserToAnotherUser() {
        when(ctx.getUserId()).thenReturn(1L);
        controller.followUser(FOLLOWEE_ID);
        verify(service, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(validator, times(1)).validateUserTriedFollowHimself(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnsubscribeUserFromAnotherUser() {
        when(ctx.getUserId()).thenReturn(1L);
        controller.unfollowUser(FOLLOWEE_ID);
        verify(service, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(validator, times(1)).validateUserTriedUnfollowHimself(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowerCount() {
        when(ctx.getUserId()).thenReturn(1L);
        controller.getFollowersCount();
        verify(service, times(1)).getFollowersCount(FOLLOWER_ID);
    }
}