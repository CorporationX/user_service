package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidateException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

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

    private SkillDto skillDto;
    private Skill skill;
    private User user;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("Java Programming");

        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java Programming");

        user = new User();
        user.setId(1L);
    }


    @Test
    public void testCreateSkill_WhenSkillDoesNotExist_ShouldCreateSkill() {

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto createdSkill = skillService.create(skillDto);

        assertNotNull(createdSkill);
        assertEquals(skillDto.getTitle(), createdSkill.getTitle());
        verify(skillRepository).save(skill);
    }

    @Test
    public void testCreateSkill_WhenSkillAlreadyExists_ShouldThrowException() {

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            skillService.create(skillDto);
        });
    }

    @Test
    public void testGetUserSkills_ShouldReturnListOfSkills() {
        long userId = 1L;
        List<Skill> userSkills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(userSkills);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        List<SkillDto> retrievedSkills = skillService.getUserSkills(userId);

        assertNotNull(retrievedSkills);
        assertEquals(1, retrievedSkills.size());
        assertEquals(skillDto, retrievedSkills.get(0));
    }

    @Test
    public void testGetOfferedSkills_ShouldReturnSkillCandidates() {
        long userId = 1L;
        List<Skill> offeredSkills = List.of(skill);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        List<SkillCandidateDto> candidateSkills = skillService.getOfferedSkills(userId);

        assertNotNull(candidateSkills);
        assertEquals(1, candidateSkills.size());
        assertEquals(skillDto, candidateSkills.get(0).getSkill());
        assertEquals(1L, candidateSkills.get(0).getOffersAmount());
    }

    @Test
    public void testAcquireSkillFromOffers_WhenEligible_ShouldAssignSkill() {
        long skillId = 1L;
        long userId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        List<SkillOffer> offers = createMockSkillOffers(3, skillId, userId);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto acquiredSkill = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(acquiredSkill);
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository).saveAll(anyList());
    }

    @Test
    public void testAcquireSkillFromOffers_WhenSkillAlreadyOwned_ShouldThrowException() {
        long skillId = 1L;
        long userId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));

        assertThrows(DataValidateException.class, () -> {
            skillService.acquireSkillFromOffers(skillId, userId);
        });
    }

    @Test
    public void testAcquireSkillFromOffers_WhenNotEnoughOffers_ShouldThrowException() {
        // Arrange
        long skillId = 1L;
        long userId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(createMockSkillOffers(2, skillId, userId));

        assertThrows(IllegalArgumentException.class, () -> {
            skillService.acquireSkillFromOffers(skillId, userId);
        });
    }

    private List<SkillOffer> createMockSkillOffers(int count, long skillId, long userId) {
        List<SkillOffer> offers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SkillOffer offer = new SkillOffer();
            Skill skillToOffer = new Skill();
            skillToOffer.setId(skillId);

            Recommendation recommendation = new Recommendation();
            User receiver = new User();
            receiver.setId(userId);
            User author = new User();
            author.setId(i + 2L);

            recommendation.setReceiver(receiver);
            recommendation.setAuthor(author);

            offer.setSkill(skillToOffer);
            offer.setRecommendation(recommendation);

            offers.add(offer);
        }
        return offers;
    }
}