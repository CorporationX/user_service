package school.faang.user_service.service.RecommendationTest;

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
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utils.validator.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Spy
    private RecommendationDtoValidator recommendationDtoValidator;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    private RecommendationDto recommendationDto;
    private User user;
    @InjectMocks
    private RecommendationService recommendationService;
    @BeforeEach
    void setUp(){
        recommendationDto = new RecommendationDto();
        recommendationDto.setId(3L);
        recommendationDto.setContent("Привет мир");
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setCreatedAt(LocalDateTime.now().minusMonths(7));
        recommendationDto.setSkillOffers(new ArrayList<>());

        user = new User();
        user.setId(2L);
    }

    @Test
    public void testCreate_Successful(){
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        recommendationService.create(recommendationDto);
        Mockito.verify(recommendationRepository).save(Mockito.any());
    }

    @Test
    public void testCreate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .content("Hi")
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));
    }

    @Test
    public void testCreate_RecommendationLessThenSixMonth_ShouldThrowException(){
        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.of(Recommendation
                        .builder()
                        .createdAt(LocalDateTime.now().minusMonths(5))
                        .build()));

        assertThrows(DataValidationException.class,
                ()-> recommendationService.create(RecommendationDto
                        .builder().authorId(1L)
                        .content("Hi")
                        .receiverId(2L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build()));

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (1L,2L);
    }

    @Test
    public void testCreate_SomeSkillsDoNotExist_ShouldThrowException(){
        Set<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .collect(Collectors.toSet());
        var skills = skillRepository.findAllById(skillIds);

        assertEquals(skillIds.size(), skills.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreate_SomeRecommendationsDoNotExist_ShouldThrowException(){
        Set<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .collect(Collectors.toSet());
        var recommendations = recommendationRepository.findAllById(recommendationIds);

        assertEquals(recommendationIds.size(), recommendations.size());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testUpdate_Successful(){
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        recommendationService.update(recommendationDto);
        Mockito.verify(recommendationRepository).save(Mockito.any());
    }

    @Test
    public void testUpdate_RecommendationIsNotFound_ShouldThrowException(){
        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,2L))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                ()-> recommendationService.update(RecommendationDto
                        .builder().authorId(1L)
                        .content("Hi")
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
                        .content("Hi")
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

    @Test
    public void testGetAllGivenRecommendations_checkNull(){
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged());
        Mockito.when(recommendationRepository
                .findAllByReceiverId(recommendationDto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(null);
        assertTrue(recommendationService.getAllUserRecommendations(recommendationDto.getAuthorId()).isEmpty());
    }
}