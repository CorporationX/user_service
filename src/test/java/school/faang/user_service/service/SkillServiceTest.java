package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
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
    @Captor
    private ArgumentCaptor<SkillDto> skillDtoCaptor;

    @Test
    public void testCreate() {
        SkillDto dto = new SkillDto("title", 1L);
        Skill skill = new Skill();
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.skillDtoToSkill(dto)).thenReturn(skill);
        when(skillMapper.skillToSkillDto(skill)).thenReturn(dto);

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
        when(skillCandidateMapper.skillToSkillCandidateDto(skill1)).thenReturn(candidateDto1);

        List<SkillCandidateDto> actualDtos = skillService.getOfferedSkills(id);
        assertEquals(skillCandidateDtos, actualDtos);
    }

    @Test
    public void testNotEnoughOffersToAcquireSkillsFromOffers() {
        long userId = 1L;
        long skillId = 2L;
        SkillDto dto1 = new SkillDto("Java", userId);
        List<SkillOffer> skillOffers = List.of(new SkillOffer());
        Skill skill1 = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill1));
        when(skillMapper.skillToSkillDto(skill1)).thenReturn(dto1);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        SkillDto actualDto = skillService.acquireSkillFromOffers(skillId, userId);
        assertEquals(dto1, actualDto);
    }

    @Test
    public void testEnoughOffersToAcquireSkillsFromOffers() {
        long userId = 1L;
        long skillId = 2L;
        SkillDto dto1 = new SkillDto("Java", userId);
        List<SkillOffer> skillOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer());
        Skill skill1 = new Skill();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill1));
        when(skillMapper.skillToSkillDto(skill1)).thenReturn(dto1);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        SkillDto actualDto = skillService.acquireSkillFromOffers(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);

        for (SkillOffer skillOffer : skillOffers) {
            verify(skillRepository, times(3))
                    .assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
            assertEquals(dto1, actualDto);
        }
    }
}
