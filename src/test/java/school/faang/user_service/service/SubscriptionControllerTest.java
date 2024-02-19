package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.SubscriptionDto;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;
    @Mock
    private SubscriptionService subscriptionService;

    @Test
    public void followUser() {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionController.followUser(subscriptionDto);
        verify(subscriptionService, times(1)).followUser(subscriptionDto);
    }
}
