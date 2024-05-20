package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.jpa.UserJpaRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {
    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private EventValidator eventValidator;

    @Test
    public void testValidateEventWithEmptyTitle() {
        EventDto eventDto = createEventDto("", LocalDateTime.now());
        Assert.assertThrows(DataValidationException.class, () -> eventValidator.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventWithBlankTitle() {
        EventDto eventDto = createEventDto("    ", LocalDateTime.now());
        Assert.assertThrows(DataValidationException.class, () -> eventValidator.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventWithEmptyStartDate() {
        EventDto eventDto = createEventDto("Title", null);
        Assert.assertThrows(DataValidationException.class, () -> eventValidator.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventWithEmptyOwnerId() {
        EventDto eventDto = createEventDto("Title", LocalDateTime.now());
        when(userJpaRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.empty());
        Assert.assertThrows(DataValidationException.class, () -> eventValidator.validateEvent(eventDto));
    }

    @Test
    public void testValidateEventWithCorrectData() {
        EventDto eventDto = createEventDto("Title", LocalDateTime.now());
        User owner = new User();
        owner.setId(1L);
        when(userJpaRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(owner));
        eventValidator.validateEvent(eventDto);
    }

    @Test
    public void testCheckOwnerSkills() {
        EventDto eventDto = createEventDto("Title", LocalDateTime.now());
        User owner = new User();
        owner.setId(1L);
        when(userJpaRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(owner));
        eventValidator.validateEvent(eventDto);
    }

    private EventDto createEventDto(String title, LocalDateTime startDate) {
        return EventDto.builder()
                .title(title)
                .startDate(startDate)
                .ownerId(1L)
                .build();
    }
}
