package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventMapperTest {
    @InjectMocks
    private EventMapperImpl eventMapper;
    private long eventId = 1L;
    private long ownerId = 2L;

    Event event = Event.builder()
            .id(eventId)
            .title("event title")
            .maxAttendees(5)
            .build();

    EventDto eventDto = EventDto.builder()
            .id(eventId)
            .title("event title")
            .ownerId(ownerId)
            .maxAttendees(5)
            .build();

    @Test
    public void testToEntity() {
        Event result = eventMapper.toEntity(eventDto);

        assertEquals(event, result);
    }

    @Test
    public void testToDto() {
        User user = User.builder()
                .id(ownerId)
                .build();
        event.setOwner(user);

        EventDto result = eventMapper.toDto(event);

        assertEquals(eventDto, result);
    }

    @Test
    public void testUpdateEntity() {
        String description = "new description";
        EventDto updateDto = EventDto.builder()
                .description(description)
                .build();

        eventMapper.updateEntity(updateDto, event);

        assertEquals(description, event.getDescription());
        assertEquals(eventId, event.getId());
    }
}
