package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(value = {MockitoExtension.class})
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    private SubscriptionService subscriptionService;
    private User user1;
    private User user2;
    private UserMapper userMapper;
    private List<UserFilter> userFilterList;

    @BeforeEach
    public void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper, userFilterList);
        user1 = User.builder().id(Mockito.anyLong() + 1).build();
        user2 = User.builder().id(Mockito.anyLong() + 1).build();
    }

    @Test
    public void subscribeWhen_SubscriptionExists() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(user1.getId(), user2.getId()));
    }

    @Test
    public void userFollowedSuccess() {
        subscriptionService.followUser(user1.getId(), user2.getId());
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(user1.getId(), user2.getId());
    }

    @Test
    public void unsubscribeWhen_SubscriptionDoesNotExists() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(user1.getId(), user2.getId()));
    }

    @Test
    public void userUnfollowedSuccess() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId())).thenReturn(true);
        subscriptionService.unfollowUser(user1.getId(), user2.getId());
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(user1.getId(), user2.getId());
    }

}
