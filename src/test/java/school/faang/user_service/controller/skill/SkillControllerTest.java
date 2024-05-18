package school.faang.user_service.controller.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Mock
    private SkillValidator skillValidator;

    private Long userIdOne;
    private Long userIdTwo;
    private Long skillId;
    private SkillDto skillDto;

    @BeforeEach
    public void setUp() {
        skillDto = SkillDto.builder().id(1L).title("title").build();
        userIdOne = 1L;
        userIdTwo = 2L;
        skillId = 1L;
    }

    @Test
    public void testCreateSkill() {
        when(skillService.create(skillDto)).thenReturn(skillDto);

        SkillDto result = skillController.create(skillDto);

        assertEquals(skillDto, result);
        verify(skillService).create(skillDto);
    }

    @Test
    public void testGetUsersSkill() {
        List<SkillDto> expectedSkills = Arrays.asList(new SkillDto(userIdOne,"firstTestInfo"), new SkillDto(userIdTwo, "secondTestInfo"));
        when(skillService.getUserSkills(userIdOne)).thenReturn(expectedSkills);

        List<SkillDto> actualSkills = skillController.getUserSkills(userIdOne);

        assertEquals(expectedSkills, actualSkills);
        verify(skillService).getUserSkills(userIdOne);
    }

    @Test
    public void testGetOfferedSkills() {
        List<SkillCandidateDto> expectedSkillCandidateDto = Arrays.asList
                (new SkillCandidateDto(new SkillDto(userIdOne,"firstTestInfo"), 1L),
                        new SkillCandidateDto(new SkillDto(userIdTwo, "secondTestInfo"), 2L));
        when(skillService.getOfferedSkills(userIdOne)).thenReturn(expectedSkillCandidateDto);

        List<SkillCandidateDto> candidateDtoList = skillController.getOfferedSkills(userIdOne);

        assertEquals(expectedSkillCandidateDto, candidateDtoList);
    }

    @Test
    public void testAcquireSkillFromOffers() {
        when(skillService.acquireSkillFromOffers(skillId, userIdOne)).thenReturn(skillDto);

        SkillDto skillDtoList = skillController.acquireSkillFromOffers(skillId, userIdOne);

        assertEquals(skillDto, skillDtoList);
    }
}
