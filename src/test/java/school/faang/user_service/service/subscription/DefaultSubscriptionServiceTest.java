package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilterService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultSubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper;

    @Mock
    private UserFilterService userFilterService;

    @InjectMocks
    private DefaultSubscriptionService defaultSubscriptionService;

    @Test
    void followUser() {
    }

    @Test
    void unfollowUser() {
    }

    @Test
    void getFollowers() {
    }

    @Test
    void getFollowings() {
    }

    @Test
    void getFollowersCount() {
    }

    @Test
    void getFollowingsCount() {
    }
}
