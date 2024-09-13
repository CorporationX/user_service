package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.candidate.skill.SkillCandidateValidator;
import school.faang.user_service.validator.skill.SkillValidator;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @InjectMocks
    private SkillServiceImpl skillServiceImpl;

    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper;
    @Spy
    private SkillCandidateMapperImpl skillCandidateMapperImpl;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private SkillCandidateValidator skillCandidateValidator;

    @Test
    public void testCreate() {
        SkillDto dto = new SkillDto("title", 1L);
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toSkill(dto)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(dto);

        assertEquals(dto.getTitle(), skillServiceImpl.create(skill).getTitle());

    }

    @Test
    public void testGetOfferedSkills() {
        long id = 1L;
        SkillCandidateDto candidateDto1 = new SkillCandidateDto(new SkillDto("Java", id), 1);
        List<SkillCandidateDto> skillCandidateDtos = List.of(candidateDto1);
        Skill skill1 = new Skill();
        List<Skill> skills = List.of(skill1);
        when(skillRepository.findSkillsOfferedToUser(id)).thenReturn(skills);
       // when(skillCandidateMapperImpl.toListDto(skills)).thenReturn(skillCandidateDtos);

        List<SkillCandidateDto> actualDtos = skillServiceImpl.getOfferedSkills(id);
        assertEquals(skillCandidateDtos, actualDtos);
    }

    @Test
    public void testEnoughOffersToAcquireSkillsFromOffers() {
        long userId = 1L;
        long skillId = 2L;
        boolean existsByTitle = true;
        SkillDto dto1 = new SkillDto("Java", userId);
        Skill skill1 = new Skill();
        List<SkillOffer> skillOfferList = List.of(new SkillOffer(), new SkillOffer(),new SkillOffer());
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill1));
        when(skillMapper.toDto(skill1)).thenReturn(dto1);

        SkillDto actualDto = skillServiceImpl.acquireSkillFromOffers(skillId, userId);
        verify(skillValidator,times(1)).validateSkill(skill1,existsByTitle);
        verify(skillCandidateValidator,times(1)).validateSkillOfferSize(skillOfferList);
        assertEquals(dto1, actualDto);
    }
}
