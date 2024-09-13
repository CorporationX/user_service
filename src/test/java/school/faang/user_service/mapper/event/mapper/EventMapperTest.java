package school.faang.user_service.mapper.event.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.mapper.skill.mapper.SkillMapperImpl;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @InjectMocks
    private EventMapperImpl eventMapper;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private EventDto eventDto;
    private Event event;
    private EventFilterDto eventFilterDto;
    private Skill skill;
    private User user;

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

        eventDto = EventDto.builder()
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

        skill = Skill.builder()
                .id(1L)
                .title("title")
                .createdAt(LocalDateTime.now())
                .build();

        user = new User();

        event = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(List.of(skill))
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

        eventFilterDto = EventFilterDto.builder()
                .title("Новое событие")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

    }

    @Test
    void testEventDtoToEvent() {

        Event eventForEventDto = eventMapper.eventDtoToEvent(eventDto);
        System.out.println(eventForEventDto);
    }

    @Test
    void testEventToDto() {

        EventDto eventDtoForEvent = eventMapper.eventToDto(event);
        System.out.println(eventDtoForEvent);
    }

    @Test
    void testEventToEventFilterDto() {

        EventFilterDto eventFilterDtoForEvent = eventMapper.eventToEventFilterDto(event);
        System.out.println(eventFilterDtoForEvent);
    }

    @Test
    void testEventFilterDtoToEvent() {

        Event eventForEventFilterDto = eventMapper.eventFilterDtoToEvent(eventFilterDto);
        System.out.println(eventForEventFilterDto);
    }
}