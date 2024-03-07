package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class EventMapperTest {

    @Mock
    private SkillMapper skillMapper;
    @InjectMocks
    private EventMapperImpl eventMapper;

    private User user;
    private Skill skill;
    private SkillDto skillDto;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
        skill = Skill.builder()
                .id(1L)
                .title("Skill")
                .build();
        skillDto = SkillDto.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .build();
        event = Event.builder()
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
        eventDto = EventDto.builder()
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
    }

    @Test
    void toDtoTest() {
        when(skillMapper.toDto(anyList())).thenReturn(List.of(skillDto));

        EventDto eventDto = eventMapper.toDto(event);

        assertAll(
                () -> assertEquals(eventDto.getId(), event.getId()),
                () -> assertEquals(eventDto.getTitle(), event.getTitle()),
                () -> assertEquals(eventDto.getStartDate(), event.getStartDate()),
                () -> assertEquals(eventDto.getEndDate(), event.getEndDate()),
                () -> assertEquals(eventDto.getOwnerId(), event.getOwner().getId()),
                () -> assertEquals(eventDto.getDescription(), event.getDescription()),
                () -> assertEquals(eventDto.getRelatedSkills(), skillMapper.toDto(event.getRelatedSkills())),
                () -> assertEquals(eventDto.getLocation(), event.getLocation()),
                () -> assertEquals(eventDto.getMaxAttendees(), event.getMaxAttendees())
        );
    }

    @Test
    void toEntityTest() {
        when(skillMapper.toEntity(anyList())).thenReturn(List.of(skill));

        Event event = eventMapper.toEntity(eventDto);

        assertAll(
                () -> assertEquals(event.getId(), eventDto.getId()),
                () -> assertEquals(event.getTitle(), eventDto.getTitle()),
                () -> assertEquals(event.getStartDate(), eventDto.getStartDate()),
                () -> assertEquals(event.getEndDate(), eventDto.getEndDate()),
                () -> assertEquals(event.getOwner().getId(), eventDto.getOwnerId()),
                () -> assertEquals(event.getDescription(), eventDto.getDescription()),
                () -> assertEquals(event.getRelatedSkills(), skillMapper.toEntity(eventDto.getRelatedSkills())),
                () -> assertEquals(event.getLocation(), eventDto.getLocation()),
                () -> assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees())
        );
    }
}
