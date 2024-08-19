package school.faang.user_service.service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.skill.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillController skillController;

    private final SkillDto skillDto = new SkillDto(1L, "test");

    @Test
    public void testCreate() {
        Mockito.when(skillService.create(Mockito.any())).thenReturn(skillDto);
        skillController.create(skillDto);
        Mockito.verify(skillService).create(skillDto);
    }

    @Test
    public void testGetUserSkills() {
        List<SkillDto> singleSkillDto = List.of(skillDto);
        Mockito.when(skillService.getUserSkills(1L)).thenReturn(singleSkillDto);
        skillController.getUserSkills(1L);
        Mockito.verify(skillService).getUserSkills(1L);
        Assertions.assertEquals(singleSkillDto, skillController.getUserSkills(1L));
    }

    @Test
    public void testGetOfferedSkills() {
        SkillCandidateDto singleSkillCandidateDto = new SkillCandidateDto(skillDto, 1L);
        List<SkillCandidateDto> singleSkillCandidateDtoList = List.of(singleSkillCandidateDto);
        Mockito.when(skillService.getOfferedSkills(1L)).thenReturn(singleSkillCandidateDtoList);
        skillController.getOfferedSkills(1L);
        Mockito.verify(skillService).getOfferedSkills(1L);
        Assertions.assertEquals(singleSkillCandidateDtoList, skillController.getOfferedSkills(1L));
    }

    @Test
    void testAcquireLearnedSkillFromOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(1L, 1L)).thenThrow(new DataValidationException("the skill is already learned"));
        Exception exception = Assert.assertThrows(DataValidationException.class, () -> skillController.acquireSkillFromOffers(1L, 1L));
        String expectedMessage = "the skill is already learned";
        String actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testAcquireSkillFromNotEnoughOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(1L, 1L)).thenThrow(new DataValidationException("not enough offers to acquire the skill..."));
        Exception exception = Assert.assertThrows(DataValidationException.class, () -> skillController.acquireSkillFromOffers(1L, 1L));
        String expectedMessage = "not enough offers to acquire the skill...";
        String actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testAcquireSkillFromOffers() {
        Mockito.when(skillService.acquireSkillFromOffers(1L, 1L)).thenReturn(skillDto);
        skillController.acquireSkillFromOffers(1L, 1L);
        Mockito.verify(skillService).acquireSkillFromOffers(1L, 1L);
    }

}
