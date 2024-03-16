package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.skill.SkillMapperImpl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {

    @InjectMocks
    private SkillMapperImpl skillMapper;

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
