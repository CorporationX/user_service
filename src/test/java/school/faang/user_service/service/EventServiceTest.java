package school.faang.user_service.service;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.dto.event.EventDto;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Mock
    private List eventFilters;
    @Captor
    private ArgumentCaptor<Event> captor;
    @InjectMocks
    private EventService eventService;

    public EventDto prepareData(boolean existsById) {
        EventDto dto = new EventDto();
        dto.setTitle("title");
        when(eventRepository.existsById(dto.getId())).thenReturn(existsById);
        return dto;
    }

    @Test
    public void testCreateUserWithoutSkill() {
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setUsers(List.of(firstUser, secondUser));
        secondSkill.setUsers(List.of(firstUser, secondUser));
        EventDto dto = new EventDto();
        dto.setOwnerId(3L);
        dto.setRelatedSkills(List.of(skillMapper.toDto(firstSkill), skillMapper.toDto(secondSkill)));
        assertThrows(ResourceNotFoundException.class, () -> eventService.create(dto));
    }

    @Test
    public void testCreateSaveEvent() {
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setUsers(List.of(firstUser, secondUser));
        secondSkill.setUsers(List.of(firstUser, secondUser));
        EventDto dto = prepareData(false);
        dto.setOwnerId(1L);
        when(userRepository.findById(dto.getOwnerId())).thenReturn(Optional.of(firstUser));
        dto.setRelatedSkills(skillMapper.toDto(List.of(firstSkill, secondSkill)));
        EventDto result = eventService.create(dto);
        verify(eventRepository, times(1)).save(captor.capture());
        Event event = captor.getValue();
        when(eventRepository.save(eventMapper.toEntity(dto))).thenReturn(event);
        assertEquals(dto.getTitle(), event.getTitle());
        assertEquals(dto.getOwnerId(), event.getOwner().getId());
        assertEquals(dto.getTitle(), result.getTitle());
    }

    @Test
    public void testGetEventById(){

    }

    @Test
    public void testGetEventsByFilter(){

    }

}