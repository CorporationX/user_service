package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillMapperTest {

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
     void testSkillToDtoMapping() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Test Skill");

        SkillDto skillDto = skillMapper.skillToDto(skill);

        assertEquals(skill.getId(), skillDto.getId());
        assertEquals(skill.getTitle(), skillDto.getTitle());
    }

    @Test
    void testSkillDtoToSkillMapping() {
        SkillDto skillDto = new SkillDto(2L, "Test Skill DTO");

        Skill skill = skillMapper.skillToEntity(skillDto);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }
}
