package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventMapperTest {
    @InjectMocks
    private EventMapperImpl eventMapper;
    @Mock
    private UserService userService;
    private long eventId = 1L;
    private long ownerId = 2L;
    User user = User.builder()
            .id(ownerId)
            .build();

    @Test
    public void testToEntityCreateEntity() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(ownerId);

        when(userService.findUserById(ownerId)).thenReturn(user);

        Event event = eventMapper.toEntity(eventDto, userService);

        assertNotNull(event.getOwner());
        assertEquals(ownerId, event.getOwner().getId());
        verify(userService).findUserById(ownerId);
    }

    @Test
    public void testToDto() {
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);

        EventDto eventDto = eventMapper.toDto(event);

        assertNotNull(eventDto);
        assertEquals(eventId, eventDto.getId());
        assertEquals(ownerId, eventDto.getOwnerId());
    }
}
