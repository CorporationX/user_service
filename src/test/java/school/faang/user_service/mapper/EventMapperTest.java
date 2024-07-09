package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class EventMapperTest {
    @InjectMocks
    private EventMapperImpl eventMapper;
    @Mock
    private UserRepository userRepository;
    private long eventId = 1L;
    private long ownerId = 2L;
    User user = User.builder()
            .id(ownerId)
            .build();

    @Test
    public void testToEntityCreateEntity() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        Event event = eventMapper.toEntity(eventDto, userRepository);

        assertNotNull(event.getOwner());
        assertEquals(ownerId, event.getOwner().getId());
        verify(userRepository).findById(ownerId);
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
