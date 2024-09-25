package school.faang.user_service.mapper.event.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @InjectMocks
    private EventMapperImpl eventMapper;

    @Test
    @DisplayName("success mapping EventDto to Event")
    void testToEvent() {

        SkillRepository skillRepository = mock(SkillRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Новое событие")
                .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .ownerId(1L)
                .description("description")
                .relatedSkills(List.of(1L, 2L))
                .location("location")
                .maxAttendees(5)
                .build();

        Skill skill1 = Skill.builder()
                .id(1L)
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        User user = User.builder()
                .id(1L)
                .username("Oleg")
                .build();

        List<Skill> mockSkills = List.of(skill1, skill2);

        Event event = eventMapper.toEvent(eventDto);

        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(event.getLocation(), eventDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees());
    }

    @Test
    @DisplayName("success mapping Event to EventDto")
    void testToEventDto() {

        User user = User.builder()
                .id(1L)
                .build();

        Skill skill1 = Skill.builder()
                .id(1L)
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        List<Skill> skills = List.of(skill1, skill2);

        Event event = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(skills)
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();

        EventDto eventDto = eventMapper.toDto(event);

        eventMapper.mapRelatedSkillsTargetEventDto(event, eventDto);

        assertNotNull(eventDto.getRelatedSkills());

        List<Long> eventIds = event.getRelatedSkills().stream()
                .map(Skill::getId)
                .toList();

        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(eventIds, eventDto.getRelatedSkills());
        assertEquals(event.getLocation(), eventDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees());
    }
}