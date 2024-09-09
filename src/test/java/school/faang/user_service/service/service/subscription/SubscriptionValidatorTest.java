package school.faang.user_service.service.service.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionValidator;

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
    void testValidateUser_ValidateExistUser() {
        when(subscriptionRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.validateUser(userId));
    }

    @Test
    @DisplayName("Validate non-existing user")
    void testValidateUser_ValidateNonExistUser() {
        when(subscriptionRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> subscriptionValidator.validateUser(userId));
    }

    @Test
    @DisplayName("Validate follower and followee exists")
    void testValidateUsers_ValidateFollowerAndFolloweeExists() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.validateUsers(followerId, followeeId));
    }

    @Test
    @DisplayName("Validate users with follower don't exist")
    void testValidateUsers_ValidateUsersWithFollowerDontExist() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionValidator.validateUsers(followerId, followeeId));
    }

    @Test
    @DisplayName("Validate users with followee don't exist")
    void testValidateUsers_ValidateUsersWithFolloweeDontExist() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(true);
        when(subscriptionRepository.existsById(followeeId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionValidator.validateUsers(followerId, followeeId));
    }

    @Test
    @DisplayName("Validate users with follower and followee don't exist")
    void testValidateUsers_ValidateUsersWithFollowerAndFolloweeDontExist() {
        when(subscriptionRepository.existsById(followerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionValidator.validateUsers(followerId, followeeId));
    }

    @Test
    @DisplayName("Check subscription with different follower and followee ids")
    void testValidateUsers_CheckSubscriptionWithDifferentFollowerAndFolloweeIds() {
        assertDoesNotThrow(() -> subscriptionValidator.checkSubscriptionOnHimself(followerId, followeeId));
    }

    @Test
    @DisplayName("Check subscription with same follower and followee ids")
    void testValidateUsers_CheckSubscriptionWithSameFollowerAndFolloweeIds() {
        assertThrows(ValidationException.class,
                () -> subscriptionValidator.checkSubscriptionOnHimself(followerId, followerId));
    }

    @Test
    @DisplayName("Check existing subscription with checkIfSubscriptionExist")
    void testValidateUsers_CheckExistingSubscriptionWithCheckSubscriptionExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(SubscriptionAlreadyExistsException.class,
                () -> subscriptionValidator.checkIfSubscriptionExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check non-existing subscription with checkIfSubscriptionExist")
    void testValidateUsers_CheckNonExistingSubscriptionWithCheckSubscriptionExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionValidator.checkIfSubscriptionExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check existing subscription with checkIfSubscriptionNotExist")
    void testValidateUsers_CheckExistingSubscriptionWithCheckSubscriptionNotExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertDoesNotThrow(() -> subscriptionValidator.checkIfSubscriptionNotExists(followerId, followeeId));
    }

    @Test
    @DisplayName("Check non-existing subscription with checkIfSubscriptionNotExist")
    void testValidateUsers_CheckNonExistingSubscriptionWithCheckSubscriptionNotExist() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionValidator.checkIfSubscriptionNotExists(followerId, followeeId));
    }
}
