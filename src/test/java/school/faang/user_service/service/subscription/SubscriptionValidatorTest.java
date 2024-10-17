package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    private final Long userId = 1L;
    private final Long followeeId = userId;
    private final Long followerId = 2L;

    @Test
    @DisplayName("Validate existing user")
    void testValidateUserIds_ValidateExistUser() {
        when(subscriptionRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.validateUserIds(userId));
    }

    @Test
    @DisplayName("Validate non-existing user")
    void testValidateUserIds_ValidateNonExistUser() {
        when(subscriptionRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> subscriptionValidator.validateUserIds(userId));
    }

    @Test
    @DisplayName("Check subscription with different follower and followee ids")
    void testIfSubscriptionOnHimself_CheckSubscriptionWithDifferentFollowerAndFolloweeIds() {
        assertDoesNotThrow(() -> subscriptionValidator.checkIfSubscriptionToHimself(followerId, followeeId));
    }

    @Test
    @DisplayName("Check subscription with same follower and followee ids")
    void testIfSubscriptionOnHimself_CheckSubscriptionWithSameFollowerAndFolloweeIds() {
        assertThrows(DataValidationException.class,
                () -> subscriptionValidator.checkIfSubscriptionToHimself(followerId, followerId));
    }

    @Test
    @DisplayName("Check existing subscription for exception")
    void testSubscriptionNotExists_CheckSubscriptionAlreadyExistException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(SubscriptionAlreadyExistsException.class,
                () -> subscriptionValidator.checkIfSubscriptionNotExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check non-existing subscription for not throwing exception")
    void testSubscriptionNotExists_CheckWithNonExistingSubscription() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionValidator.checkIfSubscriptionNotExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check existing subscription for not throwing exception")
    void testSubscriptionExists_CheckWithExistingSubscription() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.checkIfSubscriptionExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check non-existing subscription for exception")
    void testSubscriptionExists_CheckSubscriptionNotFoundException() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionValidator.checkIfSubscriptionExists(followerId, followeeId));
    }
}
