package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillCustomMapper;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCustomMapperTest {
    @Mock
    private SkillCustomMapper skillCustomMapper;
    @InjectMocks
    private EventCustomMapper eventCustomMapper;
    private TestDataEvent testDataEvent;
    private Skill skill1;
    private Skill skill2;
    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    public void init() {
        testDataEvent = new TestDataEvent();

        skill1 = testDataEvent.getSkill1();
        skill2 = testDataEvent.getSkill2();
        skillDto1 = testDataEvent.getSkillDto1();
        skillDto2 = testDataEvent.getSkillDto2();
    }

    @Test
    void testToDto_Success() {
        event = testDataEvent.getEvent();
        eventDto = new EventDto();

        when(skillCustomMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillCustomMapper.toDto(skill2)).thenReturn(skillDto2);

        eventDto = eventCustomMapper.toDto(event);

        assertNotNull(eventDto);
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getLocation(), eventDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertEquals(event.getType(), eventDto.getType());
        assertEquals(event.getStatus(), eventDto.getStatus());

        List<SkillDto> expectedSkillDtoList = List.of(skillDto1, skillDto2);
        assertEquals(expectedSkillDtoList, eventDto.getRelatedSkills());
    }

    @Test
    void testToEntity_Success() {
        event = new Event();
        eventDto = testDataEvent.getEventDto();

        when(skillCustomMapper.toEntity(skillDto1)).thenReturn(skill1);
        when(skillCustomMapper.toEntity(skillDto2)).thenReturn(skill2);

        event = eventCustomMapper.toEntity(eventDto);

        assertNotNull(event);
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getLocation(), eventDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertEquals(event.getType(), eventDto.getType());
        assertEquals(event.getStatus(), eventDto.getStatus());

        List<Skill> expectedSkillList = List.of(skill1, skill2);
        assertEquals(expectedSkillList, event.getRelatedSkills());
    }
}