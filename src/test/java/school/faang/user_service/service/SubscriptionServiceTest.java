package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.zip.DataFormatException;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository);
    }

    //Negative tests
    @Test
    public void followUser_exitUser() {
        Assert.assertThrows(NullPointerException.class,
                () -> subscriptionService.followUser(0L, 0L)
        );
    }

    @Test
    public void unfollowUser_exitUser() {
        Assert.assertThrows(NullPointerException.class,
                () -> subscriptionService.followUser(0L, 0L)
        );
    }
}
