package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillAlreadyAcquiredException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    void acquireSkillFromOffers_WhenUserAlreadyHasSkill_ShouldThrowSkillAlreadyAcquiredException() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));

        assertThrows(SkillAlreadyAcquiredException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID),
                "Expected SkillAlreadyAcquiredException to be thrown when the user already has the skill.");


    }

    @Test
    void acquireSkillFromOffers_WhenNotEnoughOffers_ShouldThrowDataValidationException() {
        Skill skill = new Skill();
        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(new User());
        recommendation.setAuthor(new User());

        SkillOffer offer1 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation)
                .build();

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(offer1, offer1));

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID),
                "Should throw DataValidationException when there are fewer than MIN_SKILL_OFFERS.");
    }

    @Test
    void acquireSkillFromOffers_WhenEnoughOffers_ShouldAssignSkillAndSaveGuarantors() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle("Java");

        User receiver = new User();
        receiver.setId(USER_ID);

        User guarantor1 = new User();
        guarantor1.setId(2L);

        User guarantor2 = new User();
        guarantor2.setId(3L);

        User guarantor3 = new User();
        guarantor3.setId(4L);

        Recommendation recommendation1 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor1)
                .build();

        Recommendation recommendation2 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor2)
                .build();

        Recommendation recommendation3 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor3)
                .build();

        SkillOffer offer1 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation1)
                .build();

        SkillOffer offer2 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation2)
                .build();

        SkillOffer offer3 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation3)
                .build();

        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(offer1, offer2, offer3));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(new SkillDto(SKILL_ID, "Java"));

        verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeRepository, times(3)).save(any(UserSkillGuarantee.class));

        SkillDto result = skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        assertNotNull(result, "SkillDto should not be null if the skill acquisition is successful.");
        assertEquals("Java", result.getTitle());
    }

    @Test
    void getUserSkills_ShouldReturnListOfSkillDtos() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Python");

        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillDto> skills = skillService.getUserSkills(USER_ID);

        assertNotNull(skills, "Skills list should not be null");
        assertEquals(2, skills.size());
        assertEquals("Java", skills.get(0).getTitle());
        assertEquals("Python", skills.get(1).getTitle());
    }

    @Test
    void createSkill_WhenSkillDoesNotExist_ShouldSaveAndReturnDto() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        Skill skill = new Skill();
        skill.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result, "Result should not be null");
        assertEquals("Java", result.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void createSkill_WhenSkillAlreadyExists_ShouldThrowException() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void getAllSkills_ShouldReturnListOfSkills() {
        Skill skill1 = new Skill();
        skill1.setTitle("Java");
        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        when(skillRepository.findAll()).thenReturn(List.of(skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillDto> skills = skillService.getAllSkills();

        assertNotNull(skills, "Skills list should not be null");
        assertEquals(2, skills.size());
        assertEquals("Java", skills.get(0).getTitle());
        assertEquals("Python", skills.get(1).getTitle());
    }

    @Test
    void getOfferedSkills_ShouldReturnListOfSkillCandidatesWithCounts() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Python");

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill1, skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(USER_ID);

        assertNotNull(offeredSkills, "Offered skills list should not be null");
        assertEquals(2, offeredSkills.size());

        SkillCandidateDto javaSkill = offeredSkills.stream()
                .filter(candidate -> "Java".equals(candidate.getSkill().getTitle()))
                .findFirst()
                .orElse(null);

        SkillCandidateDto pythonSkill = offeredSkills.stream()
                .filter(candidate -> "Python".equals(candidate.getSkill().getTitle()))
                .findFirst()
                .orElse(null);

        assertNotNull(javaSkill, "Java skill should be present");
        assertEquals(2, javaSkill.getOffersAmount());

        assertNotNull(pythonSkill, "Python skill should be present");
        assertEquals(1, pythonSkill.getOffersAmount());
    }
}
