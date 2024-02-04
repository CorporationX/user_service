package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillMapperTest {

    private final SkillMapper skillMapper = new SkillMapperImpl();

    @Test
    void toDto() {
        // Arrange
        Skill skill = Skill.builder().id(1).title("Skill").build();

        // Act
        SkillDto skillDto = skillMapper.toDto(skill);

        // Assert
        assertAll(
                () -> assertEquals(skill.getId(), skillDto.getId()),
                () -> assertEquals(skill.getTitle(), skillDto.getTitle())
        );
    }

    @Test
    void toEntity() {
        // Arrange
        SkillDto skillDto = new SkillDto(1L, "Skill");

        // Act
        Skill skill = skillMapper.toEntity(skillDto);

        // Assert
        assertAll(
                () -> assertEquals(skillDto.getId(), skill.getId()),
                () -> assertEquals(skillDto.getTitle(), skill.getTitle())
        );
    }
}