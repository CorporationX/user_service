package school.faang.user_service.service;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validator.ValidatorForService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private ValidatorForService validatorForService;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferMapper skillOfferMapper;
    @Mock
    private UserSkillGuarantee userSkillGuarantee;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;


    private RecommendationService recommendationService;
    @BeforeEach
    void setUp(){
        this.recommendationService = new RecommendationService(
                recommendationRepository,
                skillOfferRepository,
                validatorForService,
                recommendationMapper,
                skillRepository,
                userRepository,
                skillOfferMapper,
                userSkillGuarantee,
                userSkillGuaranteeRepository);

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.of(buildRecommendation()));
    }

    @Test
    public void testCreate_RecommendationIsNotFound_ThrowException(){
        Mockito.when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .receiverId(2L).build()));
    }

    @Test
    public void testCreate_RecommendationLessThenSixMonth_ShouldThrowException(){
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusMonths(7))
                        .build()));

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .receiverId(2L).build()));
    }

    @Test
    public void
    private Recommendation buildRecommendation(){
        return Recommendation
                .builder()
                .id(1L)
                .receiver(User.builder().build())
                .author(User.builder().build())
                .request(RecommendationRequest.builder().build())
                .content("Hello world")
                .skillOffers(new ArrayList<>())
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }
}