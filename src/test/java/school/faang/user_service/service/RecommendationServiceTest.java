package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationEventPublisher publisher;
    private RecommendationDto recommendationDto;
    private Recommendation recommendationEntity;

    @BeforeEach
    void setUp() {
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        List<SkillOfferDto> skillOffers = Collections.singletonList(skillOfferDto);
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Test recommendation")
                .skillOffers(skillOffers)
                .build();
        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        recommendationEntity = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Test recommendation")
                .skillOffers(new ArrayList<>())
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L)
                .build();

        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);
        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
        when(recommendationRepository.update(anyLong(), anyLong(), anyString())).thenReturn(recommendationEntity);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);
        when(skillOfferRepository.findById(1L)).thenReturn(Optional.of(skillOffer));
        when(skillOfferService.getSkillOffer(1L)).thenReturn(skillOffer);
    }

    @Test
    void create_shouldInvokeValidateToCreateMethod() {
        recommendationService.create(recommendationDto);
        verify(recommendationValidator).validateToCreate(recommendationDto);
    }

    @Test
    void create_shouldInvokeRepositoryCreateMethod() {
        recommendationService.create(recommendationDto);
        verify(recommendationRepository).create(1L, 2L, "Test recommendation");
    }

    @Test
    void create_shouldInvokeRepositoryFindByIdMethod() {
        recommendationService.create(recommendationDto);
        verify(recommendationRepository).findById(1L);
    }

    @Test
    void create_shouldInvokeSkillOfferRepositoryCreateMethod() {
        recommendationService.create(recommendationDto);
        verify(skillOfferRepository).create(1L, 1L);
    }

    @Test
    void create_shouldShouldAddSkillToRecommendation() {
        assertEquals(0, recommendationEntity.getSkillOffers().size());
        recommendationService.create(recommendationDto);

        assertAll(() -> {
            assertEquals(1, recommendationEntity.getSkillOffers().size());
            assertEquals(1L, recommendationEntity.getSkillOffers().get(0).getId());
        });
    }

    @Test
    void create_shouldInvokePublisherPublishMethod() {
        recommendationService.create(recommendationDto);
        verify(publisher).publish(any(RecommendationEventDto.class));
    }

    @Test
    void create_shouldInvokeMapperToDtoMethod() {
        recommendationService.update(recommendationDto);
        verify(recommendationMapper).toDto(recommendationEntity);
    }

    @Test
    void update_shouldInvokeValidateToUpdateMethod() {
        recommendationService.update(recommendationDto);
        verify(recommendationValidator).validateToUpdate(recommendationDto);
    }

    @Test
    void update_shouldInvokeRepositoryUpdateMethod() {
        recommendationService.update(recommendationDto);
        verify(recommendationRepository).update(1L, 2L, "Test recommendation");
    }

    @Test
    void update_shouldSkillOfferRepositoryDeleteAllByRecommendationId() {
        recommendationService.update(recommendationDto);
        verify(skillOfferRepository).deleteAllByRecommendationId(1L);
    }

    @Test
    void update_shouldInvokeSkillOfferRepositoryCreateMethod() {
        recommendationService.update(recommendationDto);
        verify(skillOfferRepository).create(1L, 1L);
    }

    @Test
    void update_shouldShouldAddSkillToRecommendation() {
        assertEquals(0, recommendationEntity.getSkillOffers().size());
        recommendationService.update(recommendationDto);

        assertAll(() -> {
            assertEquals(1, recommendationEntity.getSkillOffers().size());
            assertEquals(1L, recommendationEntity.getSkillOffers().get(0).getId());
        });
    }

    @Test
    void update_shouldInvokeMapperToDtoMethod() {
        recommendationService.update(recommendationDto);
        verify(recommendationMapper).toDto(recommendationEntity);
    }

    @Test
    void delete_shouldInvokeRepositoryDeleteByIdMethod() {
        recommendationService.delete(1L);
        verify(recommendationRepository).deleteById(1L);
    }

    @Test
    void getAllUserRecommendations_shouldInvokeRepositoryFindAllByReceiverIdMethod() {
        recommendationService.getAllUserRecommendations(2L);
        verify(recommendationRepository).findAllByReceiverId(2L);
    }

    @Test
    void getAllUserRecommendations_shouldInvokeToRecommendationDtosMethod() {
        recommendationService.getAllUserRecommendations(anyLong());
        verify(recommendationMapper).toRecommendationDtos(anyList());
    }

    @Test
    void getAllGivenRecommendations_shouldInvokeRepositoryFindAllByAuthorIdMethod() {
        recommendationService.getAllGivenRecommendations(1L);
        verify(recommendationRepository).findAllByAuthorId(1L);
    }

    @Test
    void getAllGivenRecommendations_shouldInvokeToRecommendationDtosMethod() {
        recommendationService.getAllUserRecommendations(anyLong());
        verify(recommendationMapper).toRecommendationDtos(anyList());
    }
}








