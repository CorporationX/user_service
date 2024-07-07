package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EventMapperImpl.class, SkillMapperImpl.class, UserService.class})
public class EventMapperTest {
    @Autowired
    private EventMapper eventMapper;
    @Spy
    private UserService userService;

    @Test
    public void testToEntityCreateEntity() {
        long id = 1L;
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(id);

        Event event = eventMapper.toEntity(eventDto, userService);

        assertNotNull(event.getOwner());
        assertEquals(id, event.getOwner().getId());
        verify(userService).findUserById(id);
    }

    @Test
    public void testToDto() {
        long eventId = 1L;
        User owner = new User();
        long ownerId = 2L;
        owner.setId(ownerId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        EventDto eventDto = eventMapper.toDto(event);

        assertNotNull(eventDto);
        assertEquals(eventId, eventDto.getId());
        assertEquals(ownerId, eventDto.getOwnerId());
    }
}
