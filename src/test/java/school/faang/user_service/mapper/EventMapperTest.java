package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventMapperTest {
    @Autowired
    private EventMapper eventMapper;
    @MockBean
    private UserRepository userRepository;

    @Test
    public void testToEntityCreateEntity() {
        long id = 1L;
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(id);
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Event event = eventMapper.toEntity(eventDto, userRepository);

        assertNotNull(event.getOwner());
        assertEquals(id, event.getOwner().getId());
        verify(userRepository).findById(id);
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
