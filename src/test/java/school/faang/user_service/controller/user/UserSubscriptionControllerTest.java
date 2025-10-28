package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionControllerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserSubscriptionController controller;

    private long followerId;
    private long followeeId;

    @Test
    public void testFollowYourself() {
        followerId = 1L;
        followeeId = 1L;

        when(userContext.getUserId()).thenReturn(followerId);

        assertThrows(DataValidationException.class, () -> controller.followUser(followeeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testUnfollowYourself() {
        followerId = 1L;
        followeeId = 1L;

        when(userContext.getUserId()).thenReturn(followerId);

        assertThrows(DataValidationException.class, () -> controller.unfollowUser(followeeId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }
}