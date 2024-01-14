package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void should_CreateSubscription_when_NotExists() {
        long followerId = 5L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(Boolean.FALSE);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    void should_ThrowException_when_SubscriptionExists() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(Boolean.TRUE);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId));

        assertEquals("This subscription already exists", exception.getMessage());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

}