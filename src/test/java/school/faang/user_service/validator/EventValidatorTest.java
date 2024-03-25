package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testValidateNullTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> eventValidator.validateTitle(eventDto));
    }

    @Test
    public void testValidateBlankTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("   ");
        assertThrows(DataValidationException.class, () -> eventValidator.validateTitle(eventDto));
    }

    @Test
    public void testValidateGoodTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Good title!");
        assertDoesNotThrow(() -> eventValidator.validateTitle(eventDto));
    }

    @Test
    public void testValidateNullStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> eventValidator.validateStartDate(eventDto));
    }

    @Test
    public void testValidateGoodStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setStartDate(LocalDateTime.now());
        assertDoesNotThrow(() -> eventValidator.validateStartDate(eventDto));
    }

    @Test
    public void testValidateNullOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerId(eventDto));
    }

    @Test
    public void testValidateInvalidOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        when(userRepository.existsById(eventDto.getOwnerId())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> eventValidator.validateOwnerId(eventDto));
    }

    @Test
    public void testValidateGoodOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        when(userRepository.existsById(eventDto.getOwnerId())).thenReturn(true);
        assertDoesNotThrow(() -> eventValidator.validateOwnerId(eventDto));
    }
}
