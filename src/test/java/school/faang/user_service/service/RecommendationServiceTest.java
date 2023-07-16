package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private RecommendationService recommendationService;
    private RecommendationDto recommendationDto;
    private List<Skill> skills;
    private List<SkillOfferDto> skillOffers;
    private List<UserSkillGuarantee> userSkillGuarantees;
    private UserSkillGuarantee firstGuarantee;
    private UserSkillGuarantee secondGuarantee;
    private Skill sameSkill;
    private Skill userSkill;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        this.firstUser = User.builder().id(3).build();
        this.secondUser = User.builder().id(4).build();
        this.firstGuarantee = UserSkillGuarantee.builder().guarantor(firstUser).build();
        this.secondGuarantee = UserSkillGuarantee.builder().guarantor(secondUser).build();
        this.userSkillGuarantees = new ArrayList<>();
        userSkillGuarantees.add(firstGuarantee);
        userSkillGuarantees.add(secondGuarantee);
        this.sameSkill = Skill.builder().title("Java").build();
        this.userSkill = Skill.builder().title("Java").guarantees(userSkillGuarantees).build();
        this.skills = List.of(userSkill);
        this.skillOffers = List.of(new SkillOfferDto(1L, 1L, 1L));
        this.recommendationDto = new RecommendationDto(1L, 2L, 3L, "Hello", skillOffers, LocalDateTime.now());
    }

    @Test
    void createThrowExceptionWhenRecommendationLessThenSixMonth() {
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(Recommendation.builder().createdAt(LocalDateTime.now().minusDays(10)).build()));

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        Mockito.verify(recommendationRepository, Mockito.times(1)).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void createThrowExceptionWhenOneOfSkillsNotExistInDatabase() {
        SkillOfferDto basicSkillOffer = new SkillOfferDto(1L, 1L, 1L);
        SkillOfferDto nullOffer = null;
        List<SkillOfferDto> skillOffers = new ArrayList<>();
        skillOffers.add(basicSkillOffer);
        skillOffers.add(nullOffer);
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "Hello", skillOffers, LocalDateTime.now());

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(Recommendation.builder().createdAt(LocalDateTime.now().minusYears(1)).build()));


        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void saveSkillOfferThrowExceptionWhenOneOfSkillsNotExistInDatabase() {
        List<SkillOfferDto> wrongSkillOffers = List.of(new SkillOfferDto(1L, -1L, 1L), new SkillOfferDto(2L, -2L, 2L));
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "Hello", wrongSkillOffers, LocalDateTime.now());

        assertThrows(DataValidationException.class, () -> recommendationService.saveSkillOffer(recommendationDto));
    }

    @Test
    void CheckAndAddSkillsGuarantorMethodThrowException() {
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));

        assertThrows(DataValidationException.class, () -> recommendationService.saveSkillOffer(recommendationDto));
    }

    @Test
    void testSaveSkillOfferInvokesCheckAndAddSkillsGuarantorMethod() {
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        recommendationService.saveSkillOffer(recommendationDto);

        assertEquals(3, skills.get(0).getGuarantees().size());
    }

    @Test
    void testSaveSkillOfferInvokesFindAllByUserIdMethod() {
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));
        recommendationService.saveSkillOffer(recommendationDto);
        Mockito.verify(skillRepository).findAllByUserId(Mockito.anyLong());
    }

    @Test
    void testSaveSkillOfferInvokesCreatedMethod() {
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));
        recommendationService.saveSkillOffer(recommendationDto);
        Mockito.verify(skillOfferRepository).create(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void testValidateRecommendationInvokesFindFirstByAuthorIdAndReceiverIdOrderByCreatedAtDescMethod() {
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));
        recommendationService.create(recommendationDto);
        Mockito.verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void testRecommendationAreCreated() {
        Mockito.when(skillRepository.findAllByUserId(Mockito.anyLong())).thenReturn(skills);
        Mockito.when(skillRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sameSkill));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));

        recommendationService.create(recommendationDto);

        Mockito.verify(recommendationRepository).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
    }
}