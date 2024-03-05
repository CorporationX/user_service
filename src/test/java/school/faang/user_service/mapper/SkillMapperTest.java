package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SkillMapperTest {

    private final SkillMapper skillMapper = new SkillMapperImpl();

    @Test
    public void toDtoTest() {
        Skill skill = new Skill();
        skill.setTitle("Title");
        skill.setId(1);

        SkillDto skillDto = skillMapper.toDto(skill);

        assertEquals(skill.getId(), skillDto.getId());
        assertEquals(skill.getTitle(), skillDto.getTitle());
    }

    @Test
    public void toEntityTest() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Title");
        skillDto.setId(1L);

        Skill skill = skillMapper.toEntity(skillDto);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }
}
