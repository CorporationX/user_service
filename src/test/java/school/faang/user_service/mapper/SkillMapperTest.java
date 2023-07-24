package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.skill.SkillMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SkillMapperTest {
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;

    @Test
    void toDTOTest() {
        Skill skill = new Skill();
        skill.setId(1);
        skill.setTitle("title");

        SkillDto skillDTO = skillMapper.toDTO(skill);

        assertNotNull(skillDTO);
        assertEquals("title", skillDTO.getTitle());
        assertEquals(1, skillDTO.getId());
    }

    @Test
    void toEntityTest() {
        SkillDto skillDTO = new SkillDto();
        skillDTO.setId(1L);
        skillDTO.setTitle("title");

        Skill skill = skillMapper.toEntity(skillDTO);

        assertNotNull(skill);
        assertEquals("title", skill.getTitle());
        assertEquals(1, skill.getId());
    }
}