package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;

    private static final List<Long> IDS = List.of(1L);
    private static final List<Skill> SKILLS = List.of(new Skill());

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех при получении List<Skill>")
        public void whenGetSkillByIdsThenSuccess() {
            when(skillRepository.findByIdIn(IDS)).thenReturn(SKILLS);

            List<Skill> resultSkills = skillService.getSkillByIds(IDS);

            assertNotNull(resultSkills);
            assertEquals(SKILLS, resultSkills);
            verify(skillRepository).findByIdIn(IDS);
        }

        @Test
        @DisplayName("Успех при сохранении skill")
        public void whenSaveSkillThenSuccess() {
            Skill skill = new Skill();

            skillService.saveSkill(skill);

            verify(skillRepository).save(skill);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка если List равен null")
        public void whenGetSkillByIdsWithNullThenException() {
            assertThrows(DataValidationException.class, () -> skillService.getSkillByIds(null));
        }

        @Test
        @DisplayName("Ошибка если Skill равен null")
        public void whenSaveSkillWithNullThenException() {
            assertThrows(DataValidationException.class, () -> skillService.saveSkill(null));
        }
    }
}