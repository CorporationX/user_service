package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    public void EventMapperTest() {
        this.eventMapper = Mappers.getMapper(EventMapper.class);
    }

    @Test
    void shouldMapEventToEventDto() {
        User user = User.builder()
                .id(1L)
                .build();

        // Подготовка данных
        Event event = Event.builder()
                .id(1L)
                .title("Event")
                .owner(user)
                .build();

        // Выполнение маппинга
        EventDto eventDto = eventMapper.toDto(event);

        // Проверка результатов
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
    }


    @Test
    void shouldMapEventDtoToEvent() {
        // Подготовка данных
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("Event Dto")
                .ownerId(1L)
                .build();

        // Выполнение маппинга
        Event event = eventMapper.toEntity(eventDto);

        // Проверка результатов
        assertEquals(eventDto.getId(), event.getId());
        assertEquals(eventDto.getTitle(), event.getTitle());
        assertEquals(eventDto.getOwnerId(), event.getOwner().getId());
    }
}
