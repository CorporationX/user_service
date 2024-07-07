package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserService userService;

    @Test
    public void testCreateWithoutUserSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        Event event = new Event();
        event.setRelatedSkills(List.of(new Skill()));
        User user = new User();
        user.setSkills(Collections.emptyList());

        when(userService.findUserById(eventDto.getOwnerId())).thenReturn(user);
        when(eventMapper.toEntity(eventDto, userService)).thenReturn(event);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals("User hasn't required skills", exception.getMessage());
        verify(eventMapper, times(0)).toDto(eventRepository.save(event));
    }

    @Test
    public void testCreateWithRelatedSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        Event event = new Event();
        Skill skill = new Skill();
        event.setRelatedSkills(List.of(skill));
        User user = new User();
        user.setSkills(List.of(skill));

        when(userService.findUserById(eventDto.getOwnerId())).thenReturn(user);
        when(eventMapper.toEntity(eventDto, userService)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.create(eventDto);

        verify(eventRepository, times(1)).save(event);
    }

}
