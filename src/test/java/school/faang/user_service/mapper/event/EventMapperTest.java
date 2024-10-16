package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.event.EventDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventMapperTest {

    @InjectMocks
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void toDto_shouldMapEventToEventDto() {
        // Arrange
        Skill skill = Skill.builder().id(1L).build();
        Event event = Event.builder()
                .id(1L)
                .title("Event Title")
                .startDate(LocalDateTime.of(2024, 9, 10, 21, 6, 34))
                .endDate(LocalDateTime.of(2024, 9, 11, 21, 6, 34))
                .owner(User.builder().id(1L).build())
                .description("Event Description")
                .relatedSkills(List.of(skill))
                .location("Event Location")
                .maxAttendees(100)
                .build();

        // Act
        EventDto eventDto = eventMapper.toDto(event);

        // Assert
        assertEquals(1L, eventDto.id());
        assertEquals("Event Title", eventDto.title());
        assertEquals(LocalDateTime.of(2024, 9, 10, 21, 6, 34), eventDto.startDate());
        assertEquals(LocalDateTime.of(2024, 9, 11, 21, 6, 34), eventDto.endDate());
        assertEquals(1L, eventDto.ownerId());
        assertEquals("Event Description", eventDto.description());
        assertEquals(List.of(SkillDto.builder().id(1L).build()), eventDto.relatedSkills());
        assertEquals("Event Location", eventDto.location());
        assertEquals(100, eventDto.maxAttendees());
    }

    @Test
    void toEntity_shouldMapEventDtoToEvent() {
        // Arrange
        SkillDto skillDto = SkillDto.builder().id(1L).build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Event Title")
                .startDate(LocalDateTime.of(2024, 9, 10, 21, 6, 34))
                .endDate(LocalDateTime.of(2024, 9, 11, 21, 6, 34))
                .ownerId(1L)
                .description("Event Description")
                .relatedSkills(List.of(skillDto))
                .location("Event Location")
                .maxAttendees(100)
                .build();

        // Act
        Event event = eventMapper.toEntity(eventDto);

        // Assert
        assertEquals(1L, event.getId());
        assertEquals("Event Title", event.getTitle());
        assertEquals(LocalDateTime.of(2024, 9, 10, 21, 6, 34), event.getStartDate());
        assertEquals(LocalDateTime.of(2024, 9, 11, 21, 6, 34), event.getEndDate());
        assertEquals(1L, event.getOwner().getId());
        assertEquals("Event Description", event.getDescription());
        assertEquals(List.of(Skill.builder().id(1L).build()), event.getRelatedSkills());
        assertEquals("Event Location", event.getLocation());
        assertEquals(100, event.getMaxAttendees());
    }
}
