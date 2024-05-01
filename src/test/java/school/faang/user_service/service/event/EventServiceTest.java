package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.EventNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventMapper mapper;
    @InjectMocks
    private EventService service;

    private EventDto dto;
    private User owner;

    @BeforeEach
    void init() {
        dto = new EventDto();
        List<SkillDto> skillDtoList = List.of(
                new SkillDto(1L, "skill1"),
                new SkillDto(2L, "skill2")
        );
        dto.setOwnerId(1L);
        dto.setRelatedSkills(skillDtoList);

        List<Skill> skillList = List.of(
                Skill.builder().id(1L).title("skill1").build(),
                Skill.builder().id(2L).title("skill2").build()
        );
        owner = User.builder()
                .id(1L)
                .skills(skillList)
                .build();
    }

    @Test
    void createNullEvent() {
        assertThrows(NullPointerException.class, () -> service.create(null));
    }

    @Test
    void createNoOwnerEvent() {
        dto.setOwnerId(-1L);
        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(dto));
        assertEquals("owner with id=-1 not found", e.getMessage());
    }

    @Test
    void createOwnerHasNoEnoughSkills() {
        owner.setSkills(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        DataValidationException e = assertThrows(DataValidationException.class, () -> service.create(dto));
        assertEquals("user with id=1 has no enough skills to create event", e.getMessage());
    }

    @Test
    void createGoodEvent() {
        List<Skill> skillList = List.of(
                Skill.builder().id(1L).title("skill1").build(),
                Skill.builder().id(2L).title("skill2").build()
        );
        Event eventEntity = Event.builder()
                .owner(owner)
                .relatedSkills(skillList)
                .build();
        when(mapper.toEntity(dto)).thenReturn(eventEntity);
        when(mapper.toDto(eventEntity)).thenReturn(dto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        assertEquals(dto, service.create(dto));
    }

    @Test
    void getNonExistingEvent() {
        EventNotFoundException e = assertThrows(EventNotFoundException.class, () -> service.getEvent(-1L));
        assertEquals("cannot find event with id=-1", e.getMessage());
    }

    @Test
    void getExistingEvent() {
        List<Skill> skillList = List.of(
                Skill.builder().id(1L).title("skill1").build(),
                Skill.builder().id(2L).title("skill2").build()
        );
        Event eventEntity = Event.builder()
                .owner(owner)
                .relatedSkills(skillList)
                .build();
        when(mapper.toDto(eventEntity)).thenReturn(dto);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventEntity));
        assertEquals(dto, service.getEvent(1L));
    }
}