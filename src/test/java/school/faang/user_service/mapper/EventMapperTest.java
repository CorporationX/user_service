package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Spy
    @InjectMocks
    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    Event event;
    EventDto eventDto;
    User nadir;

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

    @Test
    void toEntityTest() {
        //before
        event.setOwner(null);

        //when
        Event actualEvent = eventMapper.toEntity(eventDto);

        //then
        assertEquals(event, actualEvent);
    }

    @Test
    void toDtoTest() {
        //before
        eventDto.setId(0L);
        eventDto.getRelatedSkills().forEach(skill -> skill.setId(0L));

        //when
        EventDto actualEventDto = eventMapper.toDto(event);

        //then
        assertEquals(eventDto, actualEventDto);
    }

    @Test
    void userToUserId() {
        //when
        var actualUserId = EventMapper.userToUserId(nadir);

        //then
        assertEquals(nadir.getId(), actualUserId);
    }
}