package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);

        event = new Event();
        event.setTitle("Title");
        event.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        var owner = new User();
        owner.setId(1L);
        event.setOwner(owner);
        event.setDescription("Description");

        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        event.setRelatedSkills(List.of(skillA, skillB));
        event.setLocation("Location");
        event.setMaxAttendees(10);
    }


    @Test
    void createEventPositiveTest() {
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
        when(skillMapper.toDto(List.of())).thenReturn(eventDto.getRelatedSkills());

        when(eventMapper.toEntity(eventDto)).thenReturn(event);


        assertDoesNotThrow(() -> eventService.create(eventDto));

        verify(eventRepository).save(event);
    }

    @Test
    void createEventNegativeTest() {
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
        when(skillMapper.toDto(List.of())).thenReturn(List.of());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));

        verify(eventRepository, times(0)).save(event);
        assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), exception.getMessage());
    }
}