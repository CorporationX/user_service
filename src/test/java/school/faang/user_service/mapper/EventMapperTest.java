package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Spy
    @InjectMocks
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    private Event event;
    private EventDto eventDto;
    private User nadir;

    @BeforeEach
    void setUp() {
        nadir = new User();
        nadir.setId(1L);
        nadir.setUsername("nadir");
        nadir.setEmail("nadir@gmail.com");
        nadir.setCity("Moscow");

        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        nadir.setSkills(List.of(skillA, skillB));


        event = new Event();
        event.setTitle("Title");
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 12, 12, 12);
        event.setStartDate(startDate);
        event.setOwner(nadir);
        event.setDescription("Description");
        event.setRelatedSkills(List.of(skillA, skillB));
        event.setLocation("Location");
        event.setMaxAttendees(10);


        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(startDate);
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);
    }

    @DisplayName("should map event dto to event entity")
    @Test
    void shouldMapDtoToEntity() {
        event.setOwner(null);

        Event actualEvent = eventMapper.toEntity(eventDto);

        assertEquals(event, actualEvent);
    }


    @DisplayName("should map event entity to event dto")
    @Test
    void shouldMapEntityToDto() {
        eventDto.setId(0L);
        eventDto.getRelatedSkills().forEach(skill -> skill.setId(0L));

        EventDto actualEventDto = eventMapper.toDto(event);

        assertEquals(eventDto, actualEventDto);
    }

    @DisplayName("should return userId from user")
    @Test
    void shouldReturnUserId() {
        var actualUserId = EventMapper.userToUserId(nadir);

        assertEquals(nadir.getId(), actualUserId);
    }

    @DisplayName("should map list of event entities to list of event dto")
    @Test
    void shouldMapEntityListToDtoList() {
        eventDto.setId(0L);
        eventDto.getRelatedSkills().forEach(skill -> skill.setId(0L));

        var actualEventDtoList = eventMapper.toDtos(List.of(event));

        assertEquals(List.of(eventDto), actualEventDtoList);
    }
}