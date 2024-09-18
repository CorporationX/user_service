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
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.test_data.event.TestDataEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Mock
    private SkillMapper skillCustomMapper;
    @InjectMocks
    private EventMapper eventCustomMapper;
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
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertThat(eventDto).usingRecursiveComparison()
                .ignoringFields("ownerId")
                .isEqualTo(event);
    }

    @Test
    void testToEntity_Success() {
        event = new Event();
        eventDto = testDataEvent.getEventDto();
        event = eventCustomMapper.toEntity(eventDto);

        assertNotNull(event);
        assertEquals(eventDto.getOwnerId(), event.getOwner().getId());
        assertThat(event).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringFields("owner")
                .isEqualTo(eventDto);
    }
}
