package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {
    private static final long ID = 123;
    private static final String TITLE = "title";
    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void testToDto() {
        Skill skill = new Skill();
        skill.setTitle(TITLE);
        skill.setId(ID);

        SkillDto skillDtoResult = skillMapper.toDto(skill);

        assertNotNull(skillDtoResult);
        assertEquals(skill.getId(), skillDtoResult.getId());
        assertEquals(skill.getTitle(), skillDtoResult.getTitle());
    }

    @Test
    void testToEntity() {
        SkillDto skillDto = new SkillDto(ID, TITLE);

        Skill skill = skillMapper.toEntity(skillDto);

        assertNotNull(skill);
        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }
}