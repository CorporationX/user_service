package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.*;

class SkillCandidateMapperTest {

    private final SkillCandidateMapper skillCandidateMapper = new SkillCandidateMapperImpl();

    @Test
    void toDto() {
        // Arrange
        Skill skill = Skill.builder().id(1).title("Skill").build();

        // Act
        SkillCandidateDto skillCandidateDto = skillCandidateMapper.toDto(skill);

        // Assert
        assertAll(
                () -> assertEquals(skill.getId(), skillCandidateDto.getSkillDto().getId()),
                () -> assertEquals(skill.getTitle(), skillCandidateDto.getSkillDto().getTitle())
        );
    }

    @Test
    void toEntity() {
        // Arrange
        SkillDto skillDto = new SkillDto(1L, "Skill");
        SkillCandidateDto skillCandidateDto = SkillCandidateDto.builder().skillDto(skillDto).build();

        // Act
        Skill skill = skillCandidateMapper.toEntity(skillCandidateDto);

        // Assert
        assertAll(
                () -> assertEquals(skillCandidateDto.getSkillDto().getId(), skill.getId()),
                () -> assertEquals(skillCandidateDto.getSkillDto().getTitle(), skill.getTitle())
        );
    }
}