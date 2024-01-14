package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerImplTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionControllerImpl subscriptionController;

    @Test
    void should_ReturnTrue_when_FollowIsSuccessful() {
        long followerId = 5L;
        long followeeId = 2L;

        Map.Entry<String, Boolean> result = subscriptionController.followUser(followerId, followeeId);

        assertNotNull(result);
        assertEquals("isFollowed", result.getKey());
        assertTrue(result.getValue());

        verify(subscriptionService).followUser(followerId, followeeId);
    }

    @Test
    void should_ThrowException_when_UserIdsAreInvalid() {
        long invalidFollowerId = -1L;
        long invalidFolloweeId = -2L;

        Exception exceptionForFollower = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.followUser(invalidFollowerId, 1L));
        assertEquals("User identifiers must be positive numbers", exceptionForFollower.getMessage());

        Exception exceptionForFollowee = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.followUser(1L, invalidFolloweeId)
        );
        assertEquals("User identifiers must be positive numbers", exceptionForFollowee.getMessage());

        verify(subscriptionService, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void should_ThrowException_when_FollowerAndFolloweeAreTheSame() {
        long sameUserId = 1L;

        Exception exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionController.followUser(sameUserId, sameUserId)
        );
        assertEquals("User can not be follow to himself", exception.getMessage());

        verify(subscriptionService, never()).followUser(anyLong(), anyLong());
    }

}