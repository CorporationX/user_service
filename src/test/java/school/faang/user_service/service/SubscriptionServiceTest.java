package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
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
    void shouldReturnUserDtoPage() {
        UserFilterDto filter = UserFilterDto.builder()
                .pageSize(3)
                .page(0)
                .build();
        Page<UserDto> expectedUsers = new PageImpl<>(List.of(
                new UserDto(),
                new UserDto(),
                new UserDto()
        ));
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getPageSize());


    }
}