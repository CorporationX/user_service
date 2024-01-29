package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {
    private Skill skill;
    private SkillDto skillDto;
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .title("test")
                .build();
        skillDto = SkillDto.builder()
                .title("test")
                .build();
    }

    @Test
    void testToDto() {
        SkillDto skillDto1 = skillMapper.toDto(skill);
        assertEquals(skillDto, skillDto1);
    }

    @Test
    void testToEntity() {
        Skill skill1 = skillMapper.toEntity(skillDto);
        assertEquals(skill, skill1);
    }
}