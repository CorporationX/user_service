package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class SkillMapperTest {

    @Autowired
    private SkillMapper skillMapper;

    @Test
    void toDtoTest() {
        Skill skill = Skill.builder()
                .title("Title")
                .id(1L)
                .build();

        SkillDto skillDto = skillMapper.toDto(skill);

        assertAll(
                () -> assertEquals(skill.getId(), skillDto.getId()),
                () -> assertEquals(skill.getTitle(), skillDto.getTitle())
        );
    }

    @Test
    void toEntityTest() {
        SkillDto skillDto = SkillDto.builder()
                .title("Title")
                .id(1L)
                .build();

        Skill skill = skillMapper.toEntity(skillDto);

        assertAll(
                () -> assertEquals(skillDto.getId(), skill.getId()),
                () -> assertEquals(skillDto.getTitle(), skill.getTitle())
        );
    }
}
