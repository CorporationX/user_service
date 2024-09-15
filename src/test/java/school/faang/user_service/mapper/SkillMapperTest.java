package school.faang.user_service.mapper;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class SkillMapperTest {

    @InjectMocks
    private SkillMapperImpl mapper;
    private static final long SKILL_ID_ONE = 1L;
    private static final String SOME_TITLE = "Some title";
    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(SKILL_ID_ONE)
                .title(SOME_TITLE)
                .build();
        skillDto = SkillDto.builder()
                .id(SKILL_ID_ONE)
                .title(SOME_TITLE)
                .build();
    }

    @Nested
    class ToEntity {

        @Test
        @DisplayName("If SkillDto is null then pass it further")
        public void whenDtoNullParameterMappedThenPassItToEntity() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("On enter SkillDto on exit Skill")
        public void whenDtoNotNullThenReturnEntity() {
            Skill skillResult = mapper.toEntity(skillDto);

            assertEquals(skill.getId(), skillResult.getId());
            assertEquals(skill.getTitle(), skillResult.getTitle());
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
        @DisplayName("On enter Skill on exit SkillDto")
        public void whenEntityNotNullThenReturnDto() {
            SkillDto skillDtoResult = mapper.toDto(skill);

            assertEquals(skill.getId(), skillDtoResult.getId());
            assertEquals(skill.getTitle(), skillDtoResult.getTitle());
        }
    }
}
