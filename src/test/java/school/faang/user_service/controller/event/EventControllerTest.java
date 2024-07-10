package school.faang.user_service.controller.event;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventControllerTest {


    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapper eventMapper;
    @Spy
    private UserMapper userMapper;
    @Spy
    private SkillMapper skillMapper;
    @Mock
    private List eventFilters;
    @Captor
    private ArgumentCaptor<Event> captor;
    @InjectMocks
    private EventService eventService;

    @Test
    public void
    testCreateWithBlankTitle() {
        EventDto dto = new EventDto();
        dto.setTitle(" ");
        assertThrows(DataValidationException.class, () -> eventService.create(dto));
    }

    @Test
    public void testCreateWithExistingTitle() {
        EventDto dto = prepareData(true);
        assertThrows(DataValidationException.class, () -> eventService.create(dto));
    }

    @Test
    public void testCreateSavesEvent() {
        EventDto dto = prepareData(false);
        dto.setOwnerId(1L);
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(dto.getOwnerId())).thenReturn(Optional.of(user));
        EventDto result = eventService.create(dto);
        verify(eventRepository, times(1)).save(captor.capture());
        Event event = captor.getValue();
        assertEquals(dto.getTitle(), event.getTitle());
        assertEquals(dto.getOwnerId(), event.getOwner().getId());
        assertEquals(dto.getTitle(), result.getTitle());
    }

    private EventDto prepareData(boolean existsByTitle) {
        EventDto dto = new EventDto();
        dto.setTitle("title");
        when(skillRepository.existsByTitle(dto.getTitle())).thenReturn(existsByTitle);
        return dto;
    }
}
