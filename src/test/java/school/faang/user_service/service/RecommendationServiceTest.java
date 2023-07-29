package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Spy
    private RecommendationMapper recommendationMapper;
    @Spy
    private SkillOfferMapper skillOfferMapper;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuarantee userSkillGuarantee;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationDto recommendationDto;
    @Mock
    private SkillOfferDto skillOfferDto;
    @Mock
    private SkillOffer skillOffer;
    @Mock
    private Recommendation recommendation;
    @Mock
    private Skill skill;

    @InjectMocks
    private RecommendationService recommendationService;
    @BeforeEach
    void setUp(){
        this.recommendationService = new RecommendationService(
                recommendationRepository,
                skillOfferRepository,
                recommendationMapper,
                skillRepository,
                userRepository,
                userSkillGuaranteeRepository);
    }

    @Test
    public void testCreate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));
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
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (1L,2L);
    }

    @Test
    public void testCreate_SkillNotFoundInDB_ShouldThrowException(){
        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> skillRepository.existsById(skillOffer.getSkillId()));

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testCreate_SomeSkillsDoNotExist_ShouldThrowException(){
        List<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        var skills = skillRepository.findAllById(skillIds);

        assertEquals(skillIds.size(), skills.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testCreate_SomeRecommendationsDoNotExist_ShouldThrowException(){
        List<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .toList();
        var recommendations = recommendationRepository.findAllById(recommendationIds);

        assertEquals(recommendationIds.size(), recommendations.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testUpdate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                ()-> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));
    }

    @Test
    public void testUpdate_RecommendationLessThenSixMonth_ShouldThrowException(){
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusMonths(7))
                        .build()));

        assertThrows(DataValidationException.class,
                ()-> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (1L,2L);
    }

    @Test
    public void testUpdate_SkillNotFoundInDB_ShouldThrowException(){
        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> skillRepository.existsById(skillOffer.getSkillId()));

        assertThrows(DataValidationException.class,
                () -> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testUpdate_SomeSkillsDoNotExist_ShouldThrowException(){
        List<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        var skills = skillRepository.findAllById(skillIds);

        assertEquals(skillIds.size(), skills.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testUpdate_SomeRecommendationsDoNotExist_ShouldThrowException(){
        List<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .toList();
        var recommendations = recommendationRepository.findAllById(recommendationIds);

        assertEquals(recommendationIds.size(), recommendations.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .receiverId(2L).build()));
    }

    @Test
    public void testDelete_deleteRecommendation() {
        long recommendationId = 1L;
        recommendationService.delete(recommendationId);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations_checkNull(){
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged());
        Mockito.when(recommendationRepository
                .findAllByReceiverId(recommendationDto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(null);
        assertTrue(recommendationService.getAllUserRecommendations(recommendationDto.getReceiverId()).isEmpty());
    }
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
//help