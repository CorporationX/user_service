package school.faang.user_service.contoller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.TestDataFactory;
import school.faang.user_service.validators.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Spy
    private SubscriptionValidator subscriptionValidator;

    Long firstUserId;
    Long secondUserId;

    @BeforeEach
    void setUp() {
        firstUserId = 1L;
        secondUserId = 2L;
    }

    @Test
    public void usingMethodFollowUser() {
        subscriptionController.followUser(firstUserId, secondUserId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(firstUserId, secondUserId);
    }

    @Test
    public void usingMethodUnfollowUser() {
        subscriptionController.unfollowUser(firstUserId, secondUserId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(firstUserId, secondUserId);
    }

    @Test
    public void usingMethodGetFollowers() {
        var filter = TestDataFactory.createFilterDto();
        filter.setNamePattern("petr");
        subscriptionController.getFollowers(firstUserId, filter);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(firstUserId, filter);
    }

    @Test
    public void usingMethodGetFollowersCount() {
        subscriptionController.getFollowersCount(firstUserId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(firstUserId);
    }

    @Test
    public void usingMethodGetFollowing() {
        var filter = TestDataFactory.createFilterDto();
        filter.setNamePattern("petr");
        subscriptionController.getFollowing(firstUserId, filter);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(firstUserId, filter);
    }

    @Test
    public void usingMethodGetFollowingCount() {
        subscriptionController.getFollowingCount(firstUserId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(firstUserId);
    }

}
