package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    private final long followerId = 1L;
    private final long followeeId = 2L;

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Test
    public void testUnfollowUserToAnotherUser() {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }
}