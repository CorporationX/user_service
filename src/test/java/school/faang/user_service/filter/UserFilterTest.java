package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserFilterTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private List<User> usersStream;

    @BeforeEach
    public void initUsersStream() {
        usersStream.add(new User());
        usersStream.add(new User());
        usersStream.add(new User());
    }

    @Test
    public void shouldReturnUsersListByNameFilter() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePatter("John")
                .build();
        subscriptionService = new SubscriptionService(userMapper, subscriptionRepository,
                userRepository, List.of(new UserNameFilter()));


    }
}
