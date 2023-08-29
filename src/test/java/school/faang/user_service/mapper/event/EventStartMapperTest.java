package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventStartMapperTest {
    @Spy
    private EventStartMapperImpl eventStartMapper;
    private EventStartDto expectedEventStartDto;
    private Event expectedEvent;

    @BeforeEach
    void setUp() {
        expectedEventStartDto = EventStartDto.builder()
                .id(1L)
                .attendeeIds(List.of(1L))
                .title("title")
                .startDate(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        expectedEvent = Event.builder()
                .id(1L)
                .attendees(List.of(User.builder().id(1L).build()))
                .title("title")
                .startDate(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();
    }

    @Test
    void toDto_ShouldMatchAllFields() {
        EventStartDto actualEventStartDto = eventStartMapper.toDto(expectedEvent);
        assertEquals(expectedEventStartDto, actualEventStartDto);
    }
}