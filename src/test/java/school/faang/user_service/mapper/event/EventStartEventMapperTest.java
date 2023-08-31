package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventStartEventMapperTest {

    private EventStartEventMapper eventStartEventMapper = new EventStartEventMapperImpl();

    private Event event;
    private EventStartEventDto eventStartEventDto;

    private User first;
    private User second;

    @BeforeEach
    void setUp() {
        first = User.builder()
                .id(1)
                .build();
        second = User.builder()
                .id(2)
                .build();
        event = Event.builder()
                .id(1)
                .title("Halloween")
                .attendees(List.of(first, second))
                .build();
        eventStartEventDto = EventStartEventDto.builder()
                .id(1L)
                .title("Halloween")
                .userIds(List.of(1L, 2L))
                .build();
    }

    @Test
    void toDtoTest() {
        EventStartEventDto result = eventStartEventMapper.toDto(event);

        assertEquals(eventStartEventDto, result);
    }

    @Test
    void toUserIds() {
        List<Long> result = eventStartEventMapper.toUserIds(List.of(first, second));

        assertEquals(List.of(1L, 2L), result);
    }
}