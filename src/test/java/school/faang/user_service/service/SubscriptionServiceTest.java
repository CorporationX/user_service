package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionService subscriptionService;

    @Test
    void testRepositoryFollowUser(){
        subscriptionService.followUser(22L, 23L);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testRepositoryUnfollowUser(){
        subscriptionService.unfollowUser(22L, 23L);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void testRepositoryUnfollowUserByNull(){
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(-22L, 1L));
    }

    @Test
    void testRepositoryFollowUserByNull(){
        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(-22L, 1L));
    }
}
