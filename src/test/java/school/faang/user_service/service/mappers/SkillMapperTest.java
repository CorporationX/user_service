package school.faang.user_service.service.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mappers.SkillMapperImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillMapperTest {
    @Spy
    private SkillMapperImpl skillMapper;

    @Test
    public void testDtoToSkillMapper() {
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        Skill skill = skillMapper.DtoToSkill(skillDto);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testSkillToDtoMapper() {
        Skill skill = createSkill(1L, "firstTitle");
        SkillDto skillDto = skillMapper.skillToDto(skill);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testSkillToSkillCandidateDtoMapper() {
        Skill skill = createSkill(1L, "firstTitle");
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        SkillCandidateDto skillCandidateDto = skillMapper.skillToSkillCandidateDto(skill, 1);

        SkillDto expectedSkillDto = new SkillDto(skill.getId(), skill.getTitle());
        SkillCandidateDto expectedSkillCandidateDto = new SkillCandidateDto(expectedSkillDto, 1L);

        assertEquals(expectedSkillCandidateDto, skillCandidateDto);
    }

    private SkillDto createSkillDto(long id, String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(id);
        skillDto.setTitle(title);

        return skillDto;
    }

    private Skill createSkill(long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);

        return skill;
    }
}
