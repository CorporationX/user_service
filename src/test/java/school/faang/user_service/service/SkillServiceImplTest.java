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
        SkillDto dto1 = new SkillDto("Java", userId);
        Skill skill1 = new Skill();
        SkillOffer skillOffer1 = SkillOffer.builder()
                .id(1L)
                .skill(Skill.builder().id(1L).title("Java").build())
                .recommendation(Recommendation.builder().id(1L).build())
                .build();
        SkillOffer skillOffer2 = SkillOffer.builder()
                .id(2L)
                .skill(Skill.builder().id(2L).title("Python").build())
                .recommendation(Recommendation.builder().id(2L).build())
                .build();
        SkillOffer skillOffer3 = SkillOffer.builder()
                .id(3L)
                .skill(Skill.builder().id(3L).title("JavaScript").build())
                .recommendation(Recommendation.builder().id(3L).build())
                .build();
        List<SkillOffer> skillOfferList = List.of(skillOffer1,skillOffer2,skillOffer3);
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill1));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOfferList);

        SkillDto actualDto = skillServiceImpl.acquireSkillFromOffers(skillId, userId);
        assertEquals(dto1, actualDto);
    }
}
