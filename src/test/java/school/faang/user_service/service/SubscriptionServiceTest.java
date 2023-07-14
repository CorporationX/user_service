package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followeeId = 0;

    @Test
    public void shouldReturnUserDtoPage() {
        UserFilterDto filter = UserFilterDto.builder()
                .pageSize(3)
                .page(0)
                .build();
        Page<User> desiredUsers = new PageImpl<>(List.of(
                new User(),
                new User(),
                new User()
        ));
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getPageSize());

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId, pageable))
                .thenReturn(desiredUsers);

        Page<UserDto> receivedUsers = subscriptionService.getFollowers(followeeId, filter);

        Assertions.assertEquals(desiredUsers.map(userMapper::toDto), receivedUsers);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId, pageable);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        UserFilterDto filter = UserFilterDto.builder()
                .pageSize(3)
                .page(0)
                .build();

        Mockito.when(userRepository.existsById(followeeId)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.getFollowers(followeeId, filter));
    }
}