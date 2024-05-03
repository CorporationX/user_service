package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    Skill skill;
    SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(0);
        skill.setTitle("SQL");

        skillDto = new SkillDto();
        skillDto.setId(0L);
        skillDto.setTitle("SQL");
    }

    @Test
    void toDtoTest() {
        SkillDto actualSkillDto = skillMapper.toDto(skill);

        assertEquals(skillDto, actualSkillDto);
    }

    @Test
    void toDtoListTest() {
        List<Skill> skills = List.of(skill);
        List<SkillDto> skillDtos = List.of(skillDto);


        List<SkillDto> actualSkillDtos = skillMapper.toDto(skills);


        assertEquals(skillDtos, actualSkillDtos);
    }
}