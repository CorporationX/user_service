package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceValidatorTest {
    @Mock
    EventParticipationRepository repository;
    @InjectMocks
    EventParticipationServiceValidator validator;
    User user;

    @BeforeEach
    void init() {
        user = new User();

        user.setId(1L);
    }

    @Test
    void userAlreadyRegister() {
        Mockito.when(repository.findAllParticipantsByEventId(1L))
                .thenReturn(List.of(user));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegister(1L, 1L));
    }

    @Test
    void userNotRegistered() {
        Mockito.when(repository.findAllParticipantsByEventId(1L))
                .thenReturn(List.of(user));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserUnregister(1L, 2L));
    }

    @Test
    void eventValidate() {
        Mockito.when(repository.existsById(1L))
                .thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateEvent(1L));
    }
}
