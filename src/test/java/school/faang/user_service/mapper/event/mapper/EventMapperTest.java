package school.faang.user_service.mapper.event.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.mapper.EventMapperImpl;
import school.faang.user_service.mapper.skill.mapper.SkillMapperImpl;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @InjectMocks
    private EventMapperImpl eventMapper;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto1;
    private SkillDto skillDto2;

    private EventDto eventDto;

    @BeforeEach
    void init() {
        skillDto1 = new SkillDto();
        skillDto1.setId(1L);
        skillDto1.setTitle("skill1");
        skillDto1.setCreatedAt(LocalDateTime.now());

        skillDto2 = new SkillDto();
        skillDto2.setId(2L);
        skillDto2.setTitle("skill2");
        skillDto2.setCreatedAt(LocalDateTime.now());

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Новое событие");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.of(2024, 10, 1, 12, 0));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("какое-то описание");
        eventDto.setRelatedSkills(List.of(skillDto1, skillDto2));
        eventDto.setLocation("location");
        eventDto.setMaxAttendees(5);
    }

    @Test
    void test() {
        Skill skill = skillMapper.toSkill(skillDto1);
        System.out.println(skill);

        Event afterMapper = eventMapper.eventDtoToEvent(eventDto);

        System.out.println(afterMapper);
    }

}