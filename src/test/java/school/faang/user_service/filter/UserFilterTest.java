package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserFilterTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private List<UserFilter> userFilters;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;
    private final long followerId = 2;
    private final long followeeId = 1;

/*    @Test
    public void shouldFilterFollowersByNameFilter() {
        List<User> usersStream = List.of(
                User.builder()
                        .username("MichaelJohnson")
                        .build(),
                User.builder()
                        .username("JaneSmith")
                        .build()
        );
        UserNameFilter nameFilter = Mockito.mock(UserNameFilter.class);

        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(usersStream.stream());
        List<User> filteredUsers = usersStream.stream()
                .filter();
    }*/
}
