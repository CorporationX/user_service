package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.impl.SkillServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;


    @Test
    void testGetSkillListBySkillIds_Success() {
        List<Long> skillIds = Arrays.asList(1L, 2L, 3L);
        Skill firstSkill = Skill.builder()
                .id(1L)
                .build();
        Skill secondSkill = Skill.builder()
                .id(2L)
                .build();
        Skill thirdSkill = Skill.builder()
                .id(3L)
                .build();
        List<Skill> skills = Arrays.asList(firstSkill, secondSkill, thirdSkill);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        List<Skill> result = skillService.getSkillListBySkillIds(skillIds);
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(skillRepository, times(1)).findAllById(idsCaptor.capture());
        assertEquals(skillIds, idsCaptor.getValue());
    }

    @Test
    void testGetSkillListBySkillIds_EmptyList() {
        List<Long> skillIds = Arrays.asList(-1L, -2L, -3L);
        List<Skill> skills = Collections.emptyList();
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        List<Skill> result = skillService.getSkillListBySkillIds(skillIds);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findAllById(idsCaptor.capture());
        assertEquals(skillIds, idsCaptor.getValue());
    }

}

