package school.faang.user_service.service;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void followUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    void followUserThrowIllegalException(){
        int followerId = -11;
        int followeeId = -15;
        Assert.assertThrows(IllegalArgumentException.class,
                ()-> subscriptionService.followUser(followerId, followeeId));
    }
    @Test
    void followUserThrowDataValidException() {
        int idUser = 11;
        Assert.assertThrows(DataValidationException.class,
                ()-> subscriptionService.followUser(idUser, idUser));
    }
}