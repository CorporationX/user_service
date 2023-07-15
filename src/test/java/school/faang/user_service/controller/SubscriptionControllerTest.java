package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private SubscriptionController subscriptionController;

    private final long followeeId = 0;

    @Test
    public void shouldReturnUserDtoPage() {
        UserFilterDto filter = UserFilterDto.builder()
                .pageSize(3)
                .page(0)
                .build();
        List<UserDto> expectedUsers = List.of(
                new UserDto(),
                new UserDto(),
                new UserDto()
        );

        Mockito.when(subscriptionService.getFollowers(followeeId, filter))
                .thenReturn(expectedUsers);

        List<UserDto> users = subscriptionController.getFollowers(followeeId, filter);

        Assertions.assertEquals(expectedUsers, users);
        Mockito.verify(subscriptionService).getFollowers(followeeId, filter);
    }
}