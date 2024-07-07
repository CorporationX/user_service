package school.faang.user_service.service.service_test;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.zip.DataFormatException;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository);
    }

    //Positive test

    @Test
    @DisplayName("service good follow")
    public void followTest_goodsID() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("service good unfollow")
    public void unfollowTest_goodsID() throws DataFormatException {
        //arrange
        long followerId = 0L;
        long followeeId = 1L;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }


    //Negative tests
    @Test
    @DisplayName("invalid follow test")
    public void followUser_exitUser() {
        Assert.assertThrows(DataFormatException.class,
                () -> subscriptionService.followUser(0L, 0L)
        );
    }

    @Test
    @DisplayName("invalid unfollow test")
    public void unfollowUser_exitUser() {
        Assert.assertThrows(DataFormatException.class,
                () -> subscriptionService.followUser(0L, 0L)
        );
    }
}
