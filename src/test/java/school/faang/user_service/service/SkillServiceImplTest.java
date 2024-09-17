package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
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
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapperImpl skillMapperImpl;
    @Spy
    private SkillCandidateMapperImpl skillCandidateMapperImpl;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private SkillCandidateValidator skillCandidateValidator;

    @Test
    public void testCreate() throws DataValidationException {
        SkillDto dto = new SkillDto("title", 1L);
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        when(skillRepository.save(skill)).thenReturn(skill);

        assertEquals(dto.title(), skillServiceImpl.create(skill).title());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        long skillId = 2L;
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");
        List<Skill> skillCandidates = List.of(skill1);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillCandidates);

        skillServiceImpl.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testEnoughOffersToAcquireSkillsFromOffers() throws DataValidationException {
        long userId = 1L;
        long skillId = 2L;
        SkillDto expectedDto = new SkillDto("Java", userId);
        Skill skill = new Skill();
        skill.setId(2L);
        skill.setTitle("Java");
        User user = new User();
        user.setId(2L);
        skill.setUsers(List.of(user));
        List<SkillOffer> skillOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer());
        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);


        SkillDto actualDto = skillServiceImpl.acquireSkillFromOffers(skillId, userId);

        verify(skillCandidateValidator, times(1)).validateSkillOfferSize(skillOffers);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        for (SkillOffer skillOffer : skillOffers) {
            verify(skillRepository,times(3)).assignGuarantorToUserSkill(userId, skillId, skillOffer.getId());
        }
        assertEquals(expectedDto, actualDto);
    }
}