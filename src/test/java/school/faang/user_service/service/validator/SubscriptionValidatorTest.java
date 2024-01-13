package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepo;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    public void testUserUnfollowYourself() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validateUserUnfollowYourself(1L, 1L));
    }

    @Test
    public void testNonExistsSubscription() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validateNonExistsSubscription(1L, 2L));
    }

    @Test
    public void testFollowerExists() {
        Assert.assertThrows(DataValidationException.class, () ->
                subscriptionValidator.validateExistsUser(1L));
    }
}
