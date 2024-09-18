package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplValidatorTest {
    @InjectMocks
    EventParticipationServiceValidator validator;
    User user;

    @BeforeEach
    void init() {
        user = new User();

        user.setId(1L);
    }

    @Test
    void testUserAlreadyRegister() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegister(List.of(user), 1L));
    }

    @Test
    void testUserNotRegistered() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserUnregister(List.of(user), 2L));
    }
}
