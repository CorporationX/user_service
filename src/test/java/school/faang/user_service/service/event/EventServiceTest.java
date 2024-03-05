package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @Test
    public void testUserHasRequiredSkills() {
        EventDto testEventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto()))
                .build();

        when(skillRepository.findAllByUserId(1L)).thenReturn(List.of(new Skill()));
        when(skillMapper.toEntity(any(SkillDto.class))).thenReturn(new Skill());
        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(new Event());

        EventDto returnedDtoIfNoException = eventService.create(testEventDto);
        assertEquals(testEventDto, returnedDtoIfNoException);
    }

    @Test
    public void testUserDoesntHaveRequiredSkills() {
        SkillDto requiredSkillDto = SkillDto.builder()
                .title("Required skill")
                .build();
        Skill requiredSkill = Skill.builder()
                .title(requiredSkillDto.getTitle())
                .build();
        Skill userSkill = Skill.builder()
                .title("User's skill")
                .build();
        EventDto testEventDto = EventDto.builder()
                .ownerId(1L)
                .relatedSkills(List.of(requiredSkillDto))
                .build();

        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(userSkill));
        when(skillMapper.toEntity(any(SkillDto.class))).thenReturn(requiredSkill);

        assertThrows(DataValidationException.class, () -> eventService.create(testEventDto));
    }
}
