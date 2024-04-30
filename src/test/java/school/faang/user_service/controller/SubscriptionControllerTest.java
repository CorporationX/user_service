package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        long followerId = 1L;
        long followeeId = 2L;
        when(ctx.getUserId()).thenReturn(1L);
        controller.followUser(followeeId);
        verify(service, times(1)).followUser(followerId, followeeId);
        verify(validator, times(1)).validateUserTriedHimself(followerId, followeeId);
    }
    @Test
    public void testUnsubscribeUserFromAnotherUser() {
        controller.unfollowUser(followerId, followeeId);
        verify(service, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnsubscribeUserToHimself() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> controller.unfollowUser(followerId, followerId)
        );
        assertEquals("The user " + followeeId + " tried to unfollow himself!",
                dataValidationException.getMessage()
        );
    }
}