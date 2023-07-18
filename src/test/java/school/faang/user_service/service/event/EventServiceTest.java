package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;

    @Test
    void testCreateException() {
        List<SkillDto> skillsDto = List.of(SkillDto.builder().title("test").id(1L).build());
        List<Skill> skills = List.of(Skill.builder().id(1L).build());

        User user = User.builder().id(1L).skills(skills).build();
        EventDto event = EventDto.builder().id(1L).ownerId(1L).relatedSkills(skillsDto).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(DataValidationException.class, () -> eventService.create(event));
    }

    @Test
    void testCallCreate() {
        List<SkillDto> skillsDto = List.of(SkillDto.builder().title("test").id(1L).build());
        List<Skill> skills = List.of(Skill.builder().id(1L).title("test").build());

        User user = User.builder().id(1L).skills(skills).build();
        EventDto event = EventDto.builder().id(1L).ownerId(1L).relatedSkills(skillsDto).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        eventService.create(event);
        verify(eventRepository).save(any());
    }

    @Test
    void testGetEventThrows() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void testGetEventTwo() {
        Event event = new Event();
        event.setId(1L);
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertEquals(eventDto, eventService.getEvent(1L));
    }
}