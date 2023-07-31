package school.faang.user_service.service.eventTest;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;
    EventDto eventDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventDto = new EventDto(1L, "0", LocalDateTime.now(), LocalDateTime.now(), 0L, "0", new ArrayList<>(), "location", -1);
        Mockito.when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(List.of(
                        Skill.builder().id(1).build(),
                        Skill.builder().id(2).build()
                ));
    }

    @Test
    void testSkillIsValid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(1), new SkillDto(2)));
        eventService.create(eventDto);
        Mockito.verify(eventMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void testSkillsAreInvalid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3), new SkillDto(4)));
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.create(eventDto)
        );
    }
}
