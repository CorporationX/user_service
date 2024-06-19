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
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.testData.TestData;

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
        TestData testData = new TestData();

        nadir = testData.getUser();
        event = testData.getEvent();
        eventDto = testData.getEventDto();
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
        eventDto.getRelatedSkills().forEach(skill -> skill.setId(0L));

        var actualEventDtoList = eventMapper.toDtoList(List.of(event));

        assertEquals(List.of(eventDto), actualEventDtoList);
    }
}