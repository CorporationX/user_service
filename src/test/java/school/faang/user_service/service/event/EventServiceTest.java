package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User user;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("A");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("B");
        user.setSkills(List.of(skill1, skill2));

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill1, skill2));

        eventDto = new EventDto();
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));
    }

    @Test
    public void validation_ValidEventDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> eventService.validation(eventDto));

        verify(userRepository).findById(1L);
    }

    @Test
    public void validation_TitleBlank() {
        eventDto.setTitle("");

        assertThrows(DataFormatException.class, () -> eventService.validation(eventDto));
    }

    @Test
    public void validation_NullStartDate() {
        eventDto.setStartDate(null);

        assertThrows(DataFormatException.class, () -> eventService.validation(eventDto));
    }

    @Test
    public void validation_NullOwnerId() {
        eventDto.setOwnerId(null);

        assertThrows(DataFormatException.class, () -> eventService.validation(eventDto));
    }

    @Test
    public void userContainsSkills_NoException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> eventService.userContainsSkills(eventDto));

        verify(userRepository).findById(1L);
    }

    @Test
    public void userContainsSkills_DataFormatException() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3L, "C"), new SkillDto(2L, "B")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(DataFormatException.class, () -> eventService.userContainsSkills(eventDto));

        verify(userRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnEventDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);

        EventDto createdEventDto = eventService.create(eventDto);

        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
        assertEquals(eventDto.getStartDate(), createdEventDto.getStartDate());
        assertEquals(eventDto.getOwnerId(), createdEventDto.getOwnerId());
        assertThat(eventDto.getRelatedSkills()).containsExactlyInAnyOrderElementsOf(createdEventDto.getRelatedSkills());

        verify(userRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
    }
}