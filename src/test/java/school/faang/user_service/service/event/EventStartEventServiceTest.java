package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.mapper.event.EventStartEventMapperImpl;
import school.faang.user_service.publisher.JsonObjectMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStartEventServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private JsonObjectMapper jsonObjectMapper;
    @Spy
    private EventStartEventMapper eventStartEventMapper = new EventStartEventMapperImpl();
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventStartEventService eventStartEventService;

    @Test
    void sendScheduledEventThrowException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> eventStartEventService.sendScheduledEvent(2));

        assertEquals("Event with id 2 has already been canceled", exception.getMessage());
    }

    @Test
    void sendScheduledEventTest() {
        eventStartEventService.setEventStartEventTopicName("event_start_channel");
        EventStartEventDto eventStartEventDto = EventStartEventDto.builder()
                .id(1)
                .title("Halloween")
                .userIds(List.of(1L, 2L))
                .build();
        User first = User.builder()
                .id(1)
                .build();
        User second = User.builder()
                .id(2)
                .build();
        Event event = Event.builder()
                .id(1)
                .title("Halloween")
                .attendees(List.of(first, second))
                .build();
        String json = "{\"id\": 1, \"title\": \"Halloween\", \"userIds\": [1, 2]}";

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(jsonObjectMapper.writeValueAsString(eventStartEventDto)).thenReturn(json);

        eventStartEventService.sendScheduledEvent(1);

        verify(eventRepository).findById(1L);
        verify(jsonObjectMapper).writeValueAsString(eventStartEventDto);
        verify(redisTemplate).convertAndSend("event_start_channel", json);
    }

}