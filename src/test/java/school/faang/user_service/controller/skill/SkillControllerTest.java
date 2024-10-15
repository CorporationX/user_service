package school.faang.user_service.controller.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.service.impl.skill.SkillServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillServiceImpl skillService;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = SkillDto.builder().build();
    }

    @Test
    void create_shouldReturnSkillDto() {
        // given
        when(skillService.create(skillDto)).thenReturn(skillDto);
        // when
        SkillDto result = skillController.create(skillDto);
        // then
        verify(skillService, times(1)).create(skillDto);
        assertEquals(skillDto, result);
    }

    @Test
    void getUserSkills_shouldReturnSkillDtoList() {
        // given
        long userId = 1L;
        List<SkillDto> expectedSkills = List.of(skillDto);
        when(skillService.getUserSkills(userId)).thenReturn(expectedSkills);
        // when
        List<SkillDto> result = skillController.getUserSkills(userId);
        // then
        verify(skillService, times(1)).getUserSkills(userId);
        assertEquals(expectedSkills, result);
    }

    @Test
    void getOfferedSkills_shouldReturnSkillCandidateDtoList() {
        // given
        long userId = 1L;
        long offeredSkillAmount = 2L;
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto, offeredSkillAmount);
        List<SkillCandidateDto> offeredSkills = List.of(skillCandidateDto);
        when(skillService.getOfferedSkills(userId)).thenReturn(offeredSkills);
        // when
        List<SkillCandidateDto> result = skillController.getOfferedSkills(userId);
        // then
        verify(skillService, times(1)).getOfferedSkills(userId);
        assertEquals(offeredSkills, result);
    }

    @Test
    void acquireSkillFromOffers_shouldReturnSkillDto() {
        // given
        long userId = 1L;
        long skillId = 1L;
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(skillDto);
        // when
        SkillDto result = skillController.acquireSkillFromOffers(skillId, userId);
        // then
        assertEquals(skillDto, result);
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }
}