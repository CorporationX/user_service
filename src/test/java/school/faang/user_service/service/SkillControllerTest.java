package school.faang.user_service.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import school.faang.user_service.mapper.SkillMapperImpl;

import java.util.ArrayList;
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

    @Test void testAcquireSkillFromOffers() {

    }
}
