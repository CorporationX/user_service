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
import school.faang.user_service.dto.skill.SkillAcquiredEventDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;
    @Mock
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private Skill skill;
    private User author;
    private User receiver;

    @BeforeEach
    void setUp() {
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        List<SkillOfferDto> skillOfferDtos = Collections.singletonList(skillOfferDto);
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Test recommendation")
                .skillOffers(skillOfferDtos)
                .build();

        skill = Skill.builder()
                .id(1L)
                .users(new ArrayList<>())
                .guarantees(new ArrayList<>())
                .build();

        author = User.builder()
                .id(1L)
                .build();
        receiver = User.builder()
                .id(2L)
                .skills(new ArrayList<>())
                .build();

        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .id(1L)
                .skill(skill)
                .user(receiver)
                .guarantor(author)
                .build();

        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L)
                .skill(skill)
                .build();
        List<SkillOffer> skillOffers = new ArrayList<>();
        skillOffers.add(skillOffer);

        recommendation = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Test recommendation")
                .skillOffers(skillOffers)
                .build();

        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);
        when(recommendationRepository.create(1L, 2L, "Test recommendation")).thenReturn(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        when(skillOfferRepository.create(1L, 1L)).thenReturn(1L);
        when(skillOfferRepository.findById(1L)).thenReturn(Optional.of(skillOffer));
        when(skillOfferService.getSkillOffer(1L)).thenReturn(skillOffer);
        when(userSkillGuaranteeRepository.save(any(UserSkillGuarantee.class))).thenReturn(userSkillGuarantee);
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
    void create_shouldAddNewSkillToReceiverAndPublishEvent() {
        assertEquals(0, receiver.getSkills().size());

        recommendationService.create(recommendationDto);
        assertAll(() -> {
            assertEquals(1, receiver.getSkills().size());
            assertEquals(skill, receiver.getSkills().get(0));
        });

        verify(skillAcquiredEventPublisher).publish(any(SkillAcquiredEventDto.class));
    }

    @Test
    void create_shouldAddNewGuarantorToReceiverSkill() {
        receiver.getSkills().add(skill);
        assertTrue(receiver.getSkills().get(0).getGuarantees().isEmpty());

        recommendationService.create(recommendationDto);
        assertAll(() -> {
            assertEquals(1, receiver.getSkills().get(0).getGuarantees().size());
            assertEquals(1L, receiver.getSkills().get(0).getGuarantees().get(0).getId());
            assertEquals(author, receiver.getSkills().get(0).getGuarantees().get(0).getGuarantor());
        });
    }

    @Test
    void create_shouldInvokePublisherPublishMethod() {
        recommendationService.create(recommendationDto);
        verify(recommendationEventPublisher).publish(any(RecommendationEventDto.class));
    }

    @Test
    void create_shouldInvokeMapperToDtoMethod() {
        recommendationService.create(recommendationDto);
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    void update_shouldInvokeValidateToUpdateMethod() {
        recommendationService.update(1L, recommendationDto);
        verify(recommendationValidator).validateToUpdate(recommendationDto);
    }

    @Test
    void update_shouldInvokeRepositoryUpdateMethod() {
        recommendationService.update(1L, recommendationDto);
        verify(recommendationRepository).update(1L,  "Test recommendation");
    }

    @Test
    void update_shouldSkillOfferRepositoryDeleteAllByRecommendationId() {
        recommendationService.update(1L, recommendationDto);
        verify(skillOfferRepository).deleteAllByRecommendationId(1L);
    }

    @Test
    void update_shouldInvokeSkillOfferRepositoryCreateMethod() {
        recommendationService.update(1L, recommendationDto);
        verify(skillOfferRepository).create(1L, 1L);
    }

    @Test
    void update_shouldAddNewSkillToReceiverAndPublishEvent() {
        assertEquals(0, recommendation.getReceiver().getSkills().size());

        recommendationService.update(1L,(recommendationDto));
        assertAll(() -> {
            assertEquals(1, recommendation.getReceiver().getSkills().size());
            assertEquals(skill, recommendation.getReceiver().getSkills().get(0));
        });

        verify(skillAcquiredEventPublisher).publish(any(SkillAcquiredEventDto.class));
    }

    @Test
    void update_shouldAddNewGuarantorToReceiverSkill() {
        receiver.getSkills().add(skill);
        assertTrue(receiver.getSkills().get(0).getGuarantees().isEmpty());

        recommendationService.update(1L,(recommendationDto));
        assertAll(() -> {
            assertEquals(1, receiver.getSkills().get(0).getGuarantees().size());
            assertEquals(1L, receiver.getSkills().get(0).getGuarantees().get(0).getId());
            assertEquals(author, receiver.getSkills().get(0).getGuarantees().get(0).getGuarantor());
        });
    }

    @Test
    void update_shouldInvokeMapperToDtoMethod() {
        recommendationService.update(1L, recommendationDto);
        verify(recommendationMapper).toDto(recommendation);
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








