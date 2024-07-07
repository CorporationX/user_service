package school.faang.user_service.mapper.skill;

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

    @Test
    void testToDto() {
        skill = Skill.builder()
                .id(1L)
                .title("Title")
                .build();

        skillDto = SkillDto.builder()
                .id(1L)
                .title("Title")
                .build();

        SkillDto result = skillMapper.toDto(skill);

        assertEquals(skillDto, result);
    }

    @Test
    void testToEntity() {
        skillDto = SkillDto.builder()
                .id(1L)
                .title("Title")
                .build();

        skill = Skill.builder()
                .id(1L)
                .title("Title")
                .build();

        Skill result = skillMapper.toEntity(skillDto);

        assertEquals(skill, result);
    }

    @Test
    void testToSkillCandidateDto() {
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

        SkillCandidateDto result = skillMapper.toSkillCandidateDto(skill);

        assertEquals(skillCandidateDto, result);
    }
}