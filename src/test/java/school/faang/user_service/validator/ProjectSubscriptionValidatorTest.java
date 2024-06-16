package school.faang.user_service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.user.UserService;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectSubscriptionValidatorTest {
    private static final long USER_ID = 1L;

    @Mock
    private UserService userService;
    @InjectMocks
    private ProjectSubscriptionValidator projectSubscriptionValidator;

    @Test
    public void whenValidateProjectSubscriptionAnsUserNotExistThenThrowsException() {
        when(userService.existsById(USER_ID)).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> projectSubscriptionValidator.validateProjectSubscription(USER_ID));
    }
}