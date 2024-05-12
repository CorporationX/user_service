package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillMapperTest {
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    private Skill skill;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(0);
        skill.setTitle("SQL");

        skillDto = new SkillDto();
        skillDto.setId(0L);
        skillDto.setTitle("SQL");
    }

    @DisplayName("should map skill entity to skill dto")
    @Test
    void shouldMapSkillEntityToDto() {
        SkillDto actualSkillDto = skillMapper.toDtoList(skill);

        assertEquals(skillDto, actualSkillDto);
    }

    @DisplayName("should map list of skill entities to list of skill dto")
    @Test
    void shouldMapSkillEntitiesToDtos() {
        List<Skill> skills = List.of(skill);
        List<SkillDto> skillDtos = List.of(skillDto);


        List<SkillDto> actualSkillDtos = skillMapper.toDtoList(skills);


        assertEquals(skillDtos, actualSkillDtos);
    }
}