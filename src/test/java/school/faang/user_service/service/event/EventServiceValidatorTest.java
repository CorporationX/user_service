package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventServiceValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceValidatorTest {
    @Mock
    EventRepository eventRepository;
    @InjectMocks
    EventServiceValidator validator;
    private Skill skill = new Skill();
    private User user = new User();
    private long eventId = 1L;
    private Event event = Event.builder()
            .relatedSkills(List.of(skill))
            .build();

    @Test
    public void testValidateRelatedSkillsWithoutUserSkills() {
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateRequiredSkills(user, event));
        assertEquals("User hasn't required skills", exception.getMessage());
    }

    @Test
    public void testValidateRelatedSkillsWithUserSkills() {
        user.setSkills(List.of(skill));

        assertDoesNotThrow(() -> validator.validateRequiredSkills(user, event));
    }

    @Test
    public void testValidateEventIdIncorrect() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateEventId(eventId));
        assertEquals("Event with id " + eventId + " doesn't exist", exception.getMessage());
    }

    @Test
    public void testValidateEventId() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() -> validator.validateEventId(eventId));
    }
}
