package school.faang.user_service.service;

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
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followeeId = 0;

    @Test
    public void shouldReturnUserDtoPage() {
        UserFilterDto filter = new UserFilterDto();
        Stream<User> desiredUsers = Stream.of(
                User.builder()
                        .id(0)
                        .build(),
                User.builder()
                        .id(1)
                        .build(),
                User.builder()
                        .id(2)
                        .build()
        );

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(desiredUsers);

        List<UserDto> desiredUsersDto = List.of(
                UserDto.builder()
                        .id(0L)
                        .build(),
                UserDto.builder()
                        .id(1L)
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .build()
        );

        List<UserDto> receivedUsers = subscriptionService.getFollowers(followeeId, filter);

        Assertions.assertEquals(desiredUsersDto, receivedUsers);
        Mockito.verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        UserFilterDto filter = new UserFilterDto();

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.getFollowers(followeeId, filter));
    }
}