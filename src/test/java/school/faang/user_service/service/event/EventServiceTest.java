package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper;

    @Mock
    private EventMapper eventMapper;

    @Spy
    private List<EventFilter> eventFilters;

    @InjectMocks
    private EventService eventService;

    @Test
    public void create_UserHasRequiredSkills_thenSavedToDb() {
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto()))
                .build();

        when(skillRepository.findAllByUserId(1L)).thenReturn(List.of(new Skill()));
        when(skillMapper.toEntity(any(SkillDto.class))).thenReturn(new Skill());
        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(new Event());

        Event event = eventMapper.toEntity(eventDto);
        EventDto returnedDtoIfNoException = eventService.create(eventDto);

        verify(eventRepository, times(1)).save(event);
        assertEquals(eventDto, returnedDtoIfNoException);
    }

    @Test
    public void create_UserDoesntHaveRequiredSkills_ShouldThrowDataValidationException() {
        SkillDto requiredSkillDto = SkillDto.builder()
                .title("Required skill")
                .build();
        Skill requiredSkill = Skill.builder()
                .title(requiredSkillDto.getTitle())
                .build();
        Skill userSkill = Skill.builder()
                .title("User's skill")
                .build();
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(requiredSkillDto))
                .build();

        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(userSkill));
        when(skillMapper.toEntity(any(SkillDto.class))).thenReturn(requiredSkill);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void create_UserDoesntHaveAnySkills_ShouldThrowDataValidationException() {
        SkillDto requiredSkillDto = SkillDto.builder()
                .title("Required skill")
                .build();
        Skill requiredSkill = Skill.builder()
                .title(requiredSkillDto.getTitle())
                .build();
        EventDto eventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(requiredSkillDto))
                .build();

        when(skillRepository.findAllByUserId(anyLong())).thenReturn(null);
        when(skillMapper.toEntity(any(SkillDto.class))).thenReturn(requiredSkill);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void getEvent_EventIsFound_IsValid() {
        long id = 1;
        Event event = Event.builder()
                .id(id)
                .build();
        EventDto eventDto = EventDto.builder()
                .id(event.getId())
                .build();

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        assertEquals(eventDto, eventService.getEvent(id));
    }

    @Test
    public void getEvent_EventIsNotFound_ShouldThrowNoSuchElementException() {
        long invalidId = 1;
        Event event = Event.builder()
                .id(2)
                .build();
        EventDto eventDto = EventDto.builder()
                .id(event.getId())
                .build();

        when(eventRepository.findById(invalidId)).thenReturn(Optional.ofNullable(null));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        assertThrows(NoSuchElementException.class, () ->
                eventService.getEvent(invalidId));
    }

    @Test
    public void getEventsByFilter_FiltersByTitle_IsValid() {
        EventFilterDto filters = EventFilterDto.builder()
                .title("Event")
                .build();
        Event firstEvent = Event.builder()
                .title("Event")
                .build();
        Event secondEvent = Event.builder()
                .title("Should not be in filter")
                .build();
        EventDto firstEventDto = EventDto.builder()
                .title(firstEvent.getTitle())
                .build();
        EventDto secondEventDto = EventDto.builder()
                .title(secondEvent.getTitle())
                .build();

        assertEquals(1, eventService.getEventsByFilter(filters).size());
    }
}
