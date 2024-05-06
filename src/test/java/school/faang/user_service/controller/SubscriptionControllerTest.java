package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.TestUser.FOLLOWER_ID;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;
    @Mock
    UserContext userContext;
    @Mock
    SubscriptionValidator subscriptionValidator;
    @Mock
    UserFilterDto userFilterDto;
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    public void testGetFollowers() {
        UserFilterDto filters = new UserFilterDto();
        List<UserDto> receivedUsers = List.of(new UserDto());
        subscriptionController.getFollowers(FOLLOWER_ID, filters);

        verify(subscriptionService, times(1))
                .getFollowers(FOLLOWER_ID, filters);

        when(subscriptionService.getFollowers(FOLLOWER_ID, filters))
                .thenReturn(receivedUsers);

        List<UserDto> users = subscriptionController.getFollowers(FOLLOWER_ID, filters);
        assertEquals(receivedUsers, users);
    }

}