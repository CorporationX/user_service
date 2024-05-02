package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    SkillMapper skillMapper;
    @Mock
    EventMapper eventMapper;

    @Spy
    @InjectMocks
    EventService eventService;

    EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);
    }


    @Test
    void createTest() {
        //before
        doNothing().when(eventService).checkOwnerSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills());

        //when
        eventService.create(eventDto);

        //then
        verify(eventService, times(1)).checkOwnerSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills());
        verify(eventRepository, times(1)).save(ArgumentCaptor.forClass(Event.class).capture());
    }

    @Test
    void checkOwnerSkillsPositiveTest() {
        //before
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
        when(skillMapper.toDto(List.of())).thenReturn(eventDto.getRelatedSkills());

        //when
        assertDoesNotThrow(() -> eventService.checkOwnerSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills()));

        //then
        verify(skillRepository, times(1)).findAllByUserId(eventDto.getOwnerId());
    }

    @Test
    void checkOwnerSkillsNegativeTest() {
        //before
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
        when(skillMapper.toDto(List.of())).thenReturn(List.of());

        //when
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> eventService.checkOwnerSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills()));


        //then
        verify(skillRepository, times(1)).findAllByUserId(eventDto.getOwnerId());
        assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), dataValidationException.getMessage());
    }
}