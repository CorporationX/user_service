package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SkillMapperTest {

    @Test
    void toDTOTest() {
        Skill skill = new Skill();
        skill.setId(1);
        skill.setTitle("title");

        SkillDto skillDTO = SkillMapper.INSTANCE.toSkillDTO(skill);

        assertNotNull(skillDTO);
        assertEquals("title", skillDTO.getTitle());
        assertEquals(1, skillDTO.getId());
    }

    @Test
    void toEntityTest() {
        SkillDto skillDTO = new SkillDto();
        skillDTO.setId(1L);
        skillDTO.setTitle("title");

        Skill skill = SkillMapper.INSTANCE.toEntity(skillDTO);

        assertNotNull(skill);
        assertEquals("title", skill.getTitle());
        assertEquals(1, skill.getId());
    }
}