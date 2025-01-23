package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.event.RecommendationReceivedEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.outbox.OutboxEventProcessor;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.utils.Helper;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserService userService;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private SkillService skillService;

    @Mock
    private OutboxEventProcessor outboxEventProcessor;

    @Mock
    private Helper helper;

    @InjectMocks
    private RecommendationService recommendationService;

    private RecommendationDto dto;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        dto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("initial content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .recommendationId(1L)
                        .skillId(1L)
                        .build()))
                .build();

        recommendation = recommendationMapper.toEntity(dto);
        recommendation.setSkillOffers(List.of(SkillOffer.builder().id(1L).skill(Skill.builder().id(1L).build()).build()));
    }

    @Test
    void createRecommendationFromDto() {
        when(userService.findUserById(dto.getAuthorId())).thenReturn(User.builder().id(1L).build());
        when(userService.findUserById(dto.getReceiverId())).thenReturn(User.builder().id(2L).build());
        when(skillOfferService.findAllByUserId(dto.getReceiverId())).thenReturn(List.of(SkillOffer.builder().
                id(1L)
                .skill(Skill.builder().id(1L).build())
                .build()));

        Recommendation result = recommendationService.createRecommendationFromDto(dto);
        assertNotNull(result);
        assertEquals(1L, result.getAuthor().getId());
        assertEquals(2L, result.getReceiver().getId());
        assertEquals("initial content", result.getContent());
        assertEquals(1L, result.getSkillOffers().get(0).getSkill().getId());
    }


    @Test
    void testCreateSuccessful() {
        recommendation.setId(10L);
        recommendation.setAuthor(User.builder().id(1L).build());
        recommendation.setReceiver(User.builder().id(2L).build());

        when(userService.findUserById(dto.getAuthorId())).thenReturn(User.builder().id(1L).build());
        when(userService.findUserById(dto.getReceiverId())).thenReturn(User.builder().id(2L).build());
        when(skillOfferService.findAllByUserId(dto.getReceiverId())).thenReturn(List.of(SkillOffer.builder().
                id(1L)
                .skill(Skill.builder().id(1L).build())
                .build()));
        when(skillOfferService.create(1L, 10L)).thenReturn(1L);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationDto result = recommendationService.create(dto);

        assertEquals(10L, result.getId());

        verify(recommendationValidator, times(1)).validateAuthorAndReceiverId(dto);
        verify(recommendationValidator, times(1)).validateSkillAndTimeRequirementsForGuarantee(dto);
        verify(recommendationValidator, times(1)).validateRecommendationExistsById(recommendation.getId());
        verify(userService, times(1)).findUserById(dto.getAuthorId());
        verify(userService, times(1)).findUserById(dto.getReceiverId());
        verify(skillOfferService, times(1)).findAllByUserId(dto.getReceiverId());
        verify(skillOfferService, times(1)).create(1L, recommendation.getId());
        verify(skillService, times(1)).addGuarantee(recommendation);
        verify(helper, times(1)).serializeToJson(any(RecommendationReceivedEvent.class));
        verify(outboxEventProcessor, times(1)).saveOutboxEvent(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("Recommendation create fails: author and receiver same person")
    void testCreate_AuthorSameAsReceiver_Fail() {
        doThrow(new DataValidationException("You cannot recommend yourself")).when(recommendationValidator).validateAuthorAndReceiverId(dto);

        assertThrows(DataValidationException.class, () -> recommendationService.create(dto));

        verify(recommendationValidator, times(1)).validateAuthorAndReceiverId(dto);
        verify(recommendationValidator, never()).validateSkillAndTimeRequirementsForGuarantee(dto);
    }

    @Test
    void testUpdateSuccessful() {
        dto.setId(10L);
        recommendation.setId(10L);
        when(recommendationRepository.findById(dto.getId())).thenReturn(Optional.of(recommendation));

        RecommendationDto result = recommendationService.update(dto);
        result.setContent("Updated content");

        verify(recommendationValidator, times(1)).validateAuthorAndReceiverId(dto);
        verify(recommendationValidator, times(1)).validateSkillAndTimeRequirementsForGuarantee(dto);
        verify(recommendationRepository, times(1))
                .update(anyLong(), anyLong(), anyString());
        verify(skillOfferService, times(1)).deleteAllByRecommendationId(dto.getId());
        verify(recommendationValidator, times(1)).validateRecommendationExistsById(dto.getId());
        verify(skillOfferService, times(1)).create(dto.getSkillOffers().get(0).getSkillId(), recommendation.getId());
        verify(skillService, times(1)).addGuarantee(any(Recommendation.class));

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Updated content", result.getContent());
    }

    @Test
    void testDeleteSuccessful() {
        recommendation.setId(10L);

        recommendationService.delete(recommendation.getId());

        verify(recommendationRepository, times(1)).deleteById(recommendation.getId());
    }

    @Test
    void getAllUserRecommendationsRecommendationFound() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(dto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(dto.getReceiverId());

        verify(userValidator, times(1)).validateUserById(dto.getReceiverId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("initial content", result.get(0).getContent());
    }

    @Test
    void getAllUserRecommendationsRecommendationNotFound() {
        Page<Recommendation> page = new PageImpl<>(List.of());
        when(recommendationRepository.findAllByReceiverId(dto.getReceiverId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(dto.getReceiverId());

        verify(userValidator, times(1)).validateUserById(dto.getReceiverId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getAllAuthorRecommendationsRecommendationFound() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(dto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(dto.getAuthorId());

        verify(userValidator, times(1)).validateUserById(dto.getAuthorId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("initial content", result.get(0).getContent());
    }

    @Test
    void getAllAuthorRecommendationsRecommendationNotFound() {
        Page<Recommendation> page = new PageImpl<>(List.of());
        when(recommendationRepository.findAllByReceiverId(dto.getAuthorId(), Pageable.unpaged()))
                .thenReturn(page);
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(dto.getAuthorId());

        verify(userValidator, times(1)).validateUserById(dto.getAuthorId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetRecommendationByIdNotFound() {
        recommendation.setId(10L);
        when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.empty());

        Exception result = assertThrows(EntityNotFoundException.class, () -> recommendationService.getRecommendationById(10L));
        assertEquals("Recommendation with id #" + recommendation.getId() + " not found", result.getMessage());
    }

    @Test
    void testGetRecommendationSuccess() {
        recommendation.setId(10L);
        when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.of(recommendation));

        Recommendation result = recommendationService.getRecommendationById(recommendation.getId());

        verify(recommendationRepository, times(1)).findById(recommendation.getId());

        assertEquals(recommendation.getId(), result.getId());
    }
}