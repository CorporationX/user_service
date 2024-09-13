package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventValidationTest {

    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private EventDto validEventDto;
    private EventDto invalidEventDto;
    private Skill skill1;
    private List<Skill> skills;
    private Event validEvent;
    private User user;
    private Skill skill2;
    private Event invalidEvent;

    @InjectMocks
    private EventValidation eventValidation;

    @Mock
    EventMapper eventMapper;

    @Mock
    SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        skillDto1 = SkillDto.builder()
                .id(1L)
                .title("skill1")
                .createdAt(LocalDateTime.now())
                .build();

        skillDto2 = SkillDto.builder()
                .id(2L)
                .title("skill2")
                .createdAt(LocalDateTime.now())
                .build();

        validEventDto = EventDto.builder()
                .id(1L)
                .title("Новое событие")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .ownerId(1L)
                .description("description")
                .relatedSkills(List.of(skillDto1, skillDto2))
                .location("location")
                .maxAttendees(5)
                .build();

        invalidEventDto = EventDto.builder()
                .id(null)
                .title("")
                .startDate(null)
                .ownerId(null)
                .relatedSkills(null)
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .title("title")
                .createdAt(LocalDateTime.now())
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .title("title2")
                .createdAt(LocalDateTime.now())
                .build();

        skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        user = new User();
        validEvent = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(List.of(skill1, skill2))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

        invalidEvent = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(List.of(skill1))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();
    }

    //positive test

    @Test
    void testValidateEventDtoToValidArgs() {
        eventValidation.validateEventDto(validEventDto);
    }

    @Test
    void testValidateOwnerSkillsToValidArgs() {
        when(skillRepository.findAllByUserId(1L)).thenReturn(skills);
        when(eventMapper.eventDtoToEvent(validEventDto)).thenReturn(validEvent);
        assertDoesNotThrow(() -> eventValidation.validateOwnerSkills(validEventDto));
    }

    //negative test

    @Test
    void testValidateEventDtoToInvalidationArgs() {
        assertThrows(DataValidationException.class, () -> eventValidation.validateEventDto(invalidEventDto));
    }
}