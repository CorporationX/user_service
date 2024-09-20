package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    private static final long ID = 123;
    private static final String TITLE = "squating";
    private Skill skill;
    private SkillDto skillDto;
    private List<Skill> skills;

    @BeforeEach
    public void init() {
        skillDto = SkillDto.builder()
                .id(ID)
                .title(TITLE)
                .build();
        skill = Skill.builder()
                .id(ID)
                .title(TITLE)
                .build();

        skills = List.of(skill, skill, skill);
    }

    @Test
    @DisplayName("Testing mapping entity to dto")
    void whenEntityMappedToDtoThenSuccess() {
        SkillDto skillDtoResult = skillMapper.toDto(skill);

        assertNotNull(skillDtoResult);
        assertEquals(skill.getId(), skillDtoResult.getId());
        assertEquals(skill.getTitle(), skillDtoResult.getTitle());
    }

    @Test
    @DisplayName("Testing mapping dto to entity")
    void whenDtoMappedToEntityThenSuccess() {
        Skill skillResult = skillMapper.toEntity(skillDto);

        assertNotNull(skill);
        assertEquals(skillDto.getId(), skillResult.getId());
        assertEquals(skillDto.getTitle(), skillResult.getTitle());
    }

    @Test
    @DisplayName("Testing mapping List entitys to List dtos")
    void whenListSkillsMappedToListSkillDtoThenSuccess() {
        List<SkillDto> skillDtoList = skillMapper.toSkillDtoList(skills);

        assertNotNull(skillDtoList);
        assertEquals(skills.size(), skillDtoList.size());
        assertEquals(skills.get(0).getTitle(), skillDtoList.get(0).getTitle());
        assertEquals(skills.get(2).getId(), skillDtoList.get(2).getId());
    }
}