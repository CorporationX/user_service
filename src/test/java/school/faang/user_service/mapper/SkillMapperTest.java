package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillMapperTest {
    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        skillDto = SkillDto.builder().id(1L).title("title").build();
        skill = Skill.builder().id(1L).title("title").build();
    }

    @Test
    public void testToEntityMapper() {
        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testToDtoMapper() {
        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testSkillToSkillCandidateDtoMapper() {
        SkillCandidateDto skillCandidateDto = skillMapper.skillToSkillCandidateDto(skill, 1);

        SkillDto expectedSkillDto = new SkillDto(skill.getId(), skill.getTitle());
        SkillCandidateDto expectedSkillCandidateDto = new SkillCandidateDto(expectedSkillDto, 1L);

        assertEquals(expectedSkillCandidateDto, skillCandidateDto);
    }
}
