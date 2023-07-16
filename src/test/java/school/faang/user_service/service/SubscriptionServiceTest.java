package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUnfollowUser_ThrowsExceptionOnSelfUnfollow() {
        long userId = 1;
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(userId, userId));
    }

    @Test
    public void testUnfollowUser_CallsRepositoryOnValidUnfollow() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }
}
