package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.goal.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    private long userId;
    private long goalId;
    private Skill firstSkill;
    private Skill secondSkill;
    private List<Skill> skills;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        goalId = 1L;

        firstSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setTitle("firstTitle");

        secondSkill = new Skill();
        secondSkill.setId(2L);
        secondSkill.setTitle("secondTitle");

        skills = Arrays.asList(firstSkill, secondSkill);
    }

    @DisplayName("Если Title не существует")
    @Test
    public void testExistsByTitleIfTitleNotExist() {
        when(skillRepository.existsByTitle(firstSkill.getTitle())).thenReturn(true);
        when(skillRepository.existsByTitle(secondSkill.getTitle())).thenReturn(false);

        assertFalse(skillService.existsByTitle(skills));
    }

    @DisplayName("Когда проверки title метод отработал")
    @Test
    public void testExistsByTitleWhenValid() {
        when(skillRepository.existsByTitle(firstSkill.getTitle())).thenReturn(true);
        when(skillRepository.existsByTitle(secondSkill.getTitle())).thenReturn(true);

        assertTrue(skillService.existsByTitle(skills));
    }

    @DisplayName("Метод создания успешен")
    @Test
    public void testCreateWhenValid() {
        skillService.create(skills, userId);

        verify(skillRepository, times(1)).assignSkillToUser(firstSkill.getId(), userId);
        verify(skillRepository, times(1)).assignSkillToUser(secondSkill.getId(), userId);
    }
}