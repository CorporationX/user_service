package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillMapperTest {

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    private Skill skill;
    private SkillDto skillDto;
    private SkillCandidateDto skillCandidateDto;

    @BeforeEach
    void setUp() {
        skillDto = SkillDto.builder()
                .id(1L)
                .title("Title")
                .build();

        skillCandidateDto = SkillCandidateDto.builder()
                .skill(SkillDto.builder()
                        .id(1L)
                        .title("Title")
                        .build())
                .build();

        skill = Skill.builder()
                .id(1L)
                .title("Title")
                .build();
    }

    @Test
    void testToDto() {
        SkillDto result = skillMapper.toDto(skill);

        assertEquals(skillDto.getId(), result.getId());
        assertEquals(skillDto.getTitle(), result.getTitle());
    }

    @Test
    void testToEntity() {
        Skill result = skillMapper.toEntity(skillDto);

        assertEquals(skill.getId(), result.getId());
        assertEquals(skill.getTitle(), result.getTitle());
    }

    @Test
    void testToSkillCandidateDto() {
        SkillCandidateDto result = skillMapper.toSkillCandidateDto(skill);

        assertEquals(skillCandidateDto.getSkill(), result.getSkill());
    }
}