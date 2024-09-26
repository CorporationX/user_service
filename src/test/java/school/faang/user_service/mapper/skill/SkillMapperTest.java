package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.skill.SkillMapperImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {

    private static final long SKILL_ID_ONE = 1L;
    private static final String SOME_TITLE = "Some title";

    @InjectMocks
    private SkillMapperImpl mapper;

    private Skill skill;
    private SkillDto skillDto;
    private List<Skill> skills;

    @BeforeEach
    public void init() {
        skillDto = SkillDto.builder()
                .id(SKILL_ID_ONE)
                .title(SOME_TITLE)
                .build();

        skill = Skill.builder()
                .id(SKILL_ID_ONE)
                .title(SOME_TITLE)
                .build();

        skills = List.of(skill, skill, skill);
    }

    @Test
    @DisplayName("Testing mapping List entitys to List dtos")
    void whenListSkillsMappedToListSkillDtoThenSuccess() {
        List<SkillDto> skillDtoList = mapper.toSkillDtoList(skills);

        assertNotNull(skillDtoList);
        assertEquals(skills.size(), skillDtoList.size());
        assertEquals(skills.get(0).getTitle(), skillDtoList.get(0).getTitle());
        assertEquals(skills.get(2).getId(), skillDtoList.get(2).getId());
    }

    @Nested
    class ToEntity {

        @Test
        @DisplayName("If SkillDto is null then pass it further")
        public void whenDtoNullParameterMappedThenPassItToEntity() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("Testing mapping dto to entity")
        void whenDtoMappedToEntityThenSuccess() {
            Skill skillResult = mapper.toEntity(skillDto);

            assertNotNull(skill);
            assertEquals(skillDto.getId(), skillResult.getId());
            assertEquals(skillDto.getTitle(), skillResult.getTitle());
        }
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("If Skill is null then pass it further")
        public void whenEntityNullParameterMappedThenPassItToDto() {
            assertNull(mapper.toDto(null));
        }

        @Test
        @DisplayName("Testing mapping entity to dto")
        void whenEntityMappedToDtoThenSuccess() {
            SkillDto skillDtoResult = mapper.toDto(skill);

            assertNotNull(skillDtoResult);
            assertEquals(skill.getId(), skillDtoResult.getId());
            assertEquals(skill.getTitle(), skillDtoResult.getTitle());
        }
    }
}