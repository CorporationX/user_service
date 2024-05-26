package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {
    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    private long followerId;
    private long followeeId;
    private long invalidId;

    @BeforeEach
    public void setUp() {
        followerId = 1L;
        followeeId = 2L;
        invalidId = 0L;
    }

    @Test
    public void testCheckIdIsGreaterThanZero() {
        assertThrows(DataValidationException.class, () -> subscriptionValidator.checkIdIsGreaterThanZero(invalidId));
    }

    @Test
    public void testCheckFollowerAndFolloweeAreDifferent() {
        assertThrows(DataValidationException.class, () -> subscriptionValidator.checkFollowerAndFolloweeAreDifferent(followerId, followerId));
    }

    @Test
    public void testCheckSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionValidator.checkSubscriptionExists(followerId, followeeId));
        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Test
    public void testCheckSubscriptionNotExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> subscriptionValidator.checkSubscriptionNotExists(followerId, followeeId));
        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}
