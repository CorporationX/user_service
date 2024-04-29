package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    private final long followerId = 1L;
    @Mock
    SubscriptionService service;

    @InjectMocks
    private SubscriptionController controller;

    @Test
    public void testSubscribeUserToAnotherUser() {
        long followeeId = 2L;
        controller.followUser(followerId, followeeId);
        verify(service, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testSubscribeUserToHimself() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> controller.followUser(followerId, followerId));
        assertEquals("The user " + followerId + " tried to follow himself!",
                dataValidationException.getMessage());
    }
}