package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.candidate.Skill.SkillCandidateValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private SkillCandidateMapper skillCandidateMapper;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private SkillCandidateValidator skillCandidateValidator;


    @Test
    public void testCreate() {
        SkillDto dto = new SkillDto("title", 1L);
        Skill skill = new Skill();
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toSkill(dto)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(dto);

        assertEquals(dto, skillService.create(dto));
        verify(skillRepository).save(skill);
    }

    @Test
    public void testGetOfferedSkills() {
        long id = 1L;
        SkillCandidateDto candidateDto1 = new SkillCandidateDto(new SkillDto("Java", id), 1);
        List<SkillCandidateDto> skillCandidateDtos = List.of(candidateDto1);
        Skill skill1 = new Skill();
        List<Skill> skills = List.of(skill1);
        when(skillRepository.findSkillsOfferedToUser(id)).thenReturn(skills);
        when(skillCandidateMapper.toListDto(skills)).thenReturn(skillCandidateDtos);

        List<SkillCandidateDto> actualDtos = skillService.getOfferedSkills(id);
        assertEquals(skillCandidateDtos, actualDtos);
    }

    @Test
    public void testEnoughOffersToAcquireSkillsFromOffers() {
        long userId = 1L;
        long skillId = 2L;
        SkillDto dto1 = new SkillDto("Java", userId);
        Skill skill1 = new Skill();
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill1));
        when(skillMapper.toDto(skill1)).thenReturn(dto1);

        SkillDto actualDto = skillService.acquireSkillFromOffers(skillId, userId);
        verify(skillValidator,times(1)).validateSkill(dto1);
        verify(skillCandidateValidator,times(1)).validateSkillOfferSize(skillId, userId);
        assertEquals(dto1, actualDto);
    }
}

