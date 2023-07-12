package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.dto.skill.SkillDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void TestToEntity() {
        SkillDto skillDto = new SkillDto(1L, "crek");

        Skill skillActual = skillMapper.toEntity(skillDto);

        Skill skillExpected = new Skill(1L, "crek", null, null, null, null, null, null);

        assertEquals(skillExpected, skillActual);
    }

    @Test
    void testToDto() {
        Skill skill = new Skill(1L, "crek", null, null, null, null, null, null);

        SkillDto skillDtoActual = skillMapper.toDto(skill);

        SkillDto skillDtoExpected = new SkillDto(1L, "crek");

        assertEquals(skillDtoExpected, skillDtoActual);
    }
}