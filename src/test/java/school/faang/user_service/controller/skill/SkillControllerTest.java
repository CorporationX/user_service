package school.faang.user_service.controller.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

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
    private SkillService skillService;

    @Test
    void create_shouldReturnSkillDto() {
        // given
        SkillDto skillDto = new SkillDto(1L, "Title");
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
        SkillDto skillDto1 = new SkillDto(userId, "Title 1");
        SkillDto skillDto2 = new SkillDto(userId, "Title 2");
        List<SkillDto> expectedSkills = List.of(skillDto1, skillDto2);
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
        long offeredSkillAmount1 = 2L;
        long offeredSkillAmount2 = 4L;
        SkillDto skillDto1 = new SkillDto(userId, "Title 1");
        SkillDto skillDto2 = new SkillDto(userId, "Title 2");
        SkillCandidateDto skillCandidateDto1 = new SkillCandidateDto(skillDto1, offeredSkillAmount1);
        SkillCandidateDto skillCandidateDto2 = new SkillCandidateDto(skillDto2, offeredSkillAmount2);
        List<SkillCandidateDto> offeredSkills = List.of(skillCandidateDto1, skillCandidateDto2);
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
        SkillDto skillDto = new SkillDto(userId, "Title");
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(skillDto);
        // when
        SkillDto result = skillController.acquireSkillFromOffers(skillId, userId);
        // then
        assertEquals(skillDto, result);
        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId);
    }
}