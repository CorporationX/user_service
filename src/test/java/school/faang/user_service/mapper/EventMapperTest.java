package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EventMapperTest {

    @Mock
    private SkillMapper skillMapper = new SkillMapperImpl();

    @InjectMocks
    private EventMapper eventMapper = new EventMapperImpl();

    @Test
    public void toDtoTest() {
        User user = User.builder()
                .id(1L)
                .build();

        Skill skill = Skill.builder()
                .id(1L)
                .title("Skill")
                .build();

        Event event = Event.builder()
                .id(1L)
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(15))
                .owner(user)
                .description("Description")
                .relatedSkills(List.of(skill))
                .location("Location")
                .maxAttendees(10)
                .build();

        EventDto eventDto = eventMapper.toDto(event);

        assertEquals(eventDto.getId(), event.getId());
        assertEquals(eventDto.getTitle(), event.getTitle());
        assertEquals(eventDto.getStartDate(), event.getStartDate());
        assertEquals(eventDto.getEndDate(), event.getEndDate());
        assertEquals(eventDto.getOwnerId(), event.getOwner().getId());
        assertEquals(eventDto.getDescription(), event.getDescription());
        assertEquals(eventDto.getRelatedSkills(), event.getRelatedSkills().stream()
                .map(skillMapper::toDto)
                .toList());
        assertEquals(eventDto.getLocation(), event.getLocation());
        assertEquals(eventDto.getMaxAttendees(), event.getMaxAttendees());
    }

    @Test
    public void toEntityTest() {
        SkillDto skillDto = SkillDto.builder()
                .id(1L)
                .title("Title")
                .build();

        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(15))
                .ownerId(1L)
                .description("Description")
                .relatedSkills(List.of(skillDto))
                .location("Location")
                .maxAttendees(10)
                .build();

        Event event = eventMapper.toEntity(eventDto);

        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(event.getRelatedSkills(), eventDto.getRelatedSkills().stream()
                .map(skillMapper::toEntity)
                .toList());
        assertEquals(eventDto.getLocation(), event.getLocation());
        assertEquals(eventDto.getMaxAttendees(), event.getMaxAttendees());
    }
}
