package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    void testFollowUser_valid() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void testFollowUser_followingExists() {
        long followerId = 1;
        long followeeId = 2;
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId)
        );
        assertEquals("Following is already exists", exception.getMessage());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
    }


}