package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filters.recommendation.RecommendationAuthorFilter;
import school.faang.user_service.filters.recommendation.RecommendationContentFilter;
import school.faang.user_service.filters.recommendation.RecommendationFilter;
import school.faang.user_service.filters.recommendation.RecommendationReceiverFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * unit test for RecommendationServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    private static final long AUTHOR_ID = 10L;
    private static final long ANOTHER_AUTHOR_ID = 11L;
    private static final long RECEIVER_ID = 42L;
    private static final long ANOTHER_RECEIVER_ID = 43L;

    private static final long RECOMMENDATION_ID = 100L;
    private static final long ANOTHER_RECOMMENDATION_ID = 101L; // fixed to be distinct

    private static final long SKILL_ID = 1L;
    private static final long ANOTHER_SKILL_ID = 2L;

    private static final String CONTENT = "Sample content";
    private static final String UPDATED_CONTENT = "Updated content";
    private static final String OLD_CONTENT = "Old content";

    private static User user(long id) {
        return User.builder().id(id).build();
    }

    private static Recommendation rec(long id,
                                      String content,
                                      long authorId,
                                      long receiverId) {
        return Recommendation.builder()
                .id(id)
                .content(content)
                .author(user(authorId))
                .receiver(user(receiverId))
                .build();
    }

    private static Recommendation recWithCreatedAt(
            String content,
            long authorId,
            long receiverId,
            LocalDateTime createdAt
    ) {
        return Recommendation.builder()
                .content(content)
                .author(user(authorId))
                .receiver(user(receiverId))
                .createdAt(createdAt)
                .build();
    }

    private static RecommendationResponseDto resp(long id, long authorId, long receiverId, String content) {
        return new RecommendationResponseDto(id, authorId, receiverId, content);
    }

    private static CreateRecommendationRequestDto createReq(Long receiverId, String content, List<Long> skillIds) {
        return new CreateRecommendationRequestDto(receiverId, content, skillIds);
    }

    private static UpdateRecommendationRequestDto updateReq(String content, List<Long> skillIds) {
        return new UpdateRecommendationRequestDto(content, skillIds);
    }

    private static FilterRecommendationRequestDto filters(String content, Long authorId, Long receiverId) {
        return new FilterRecommendationRequestDto(content, authorId, receiverId);
    }

    @Spy
    private RecommendationContentFilter contentFilter;
    @Spy
    private RecommendationReceiverFilter receiverFilter;
    @Spy
    private RecommendationAuthorFilter authorFilter;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserContext userContext;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "cooldownMonths", 6);
        List<RecommendationFilter> filters = List.of(contentFilter, receiverFilter, authorFilter);
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
    }

    @Test
    @DisplayName("should correctly map Recommendation entity to RecommendationResponseDto")
    void mapping_shouldMapRecommendationToResponseDto() {
        Recommendation recommendation = rec(RECOMMENDATION_ID, CONTENT, AUTHOR_ID, RECEIVER_ID);

        RecommendationResponseDto result = recommendationMapper.toResponse(recommendation);
        RecommendationResponseDto expected = resp(RECOMMENDATION_ID, AUTHOR_ID, RECEIVER_ID, CONTENT);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("create: save recommendation and return DTO")
    void create_success() {
        CreateRecommendationRequestDto input = createReq(RECEIVER_ID, CONTENT, List.of(SKILL_ID, ANOTHER_SKILL_ID));

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, CONTENT)).thenReturn(RECOMMENDATION_ID);

        Recommendation expectedRecommendation = rec(RECOMMENDATION_ID, CONTENT, AUTHOR_ID, RECEIVER_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(expectedRecommendation));
        when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

        RecommendationResponseDto actual = recommendationService.create(input);

        assertThat(actual).isEqualTo(resp(RECOMMENDATION_ID, AUTHOR_ID, RECEIVER_ID, CONTENT));

        verify(recommendationRepository).findAll();
        verify(recommendationRepository).create(AUTHOR_ID, RECEIVER_ID, CONTENT);
        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verify(recommendationMapper).toResponse(expectedRecommendation);

        verify(skillOfferRepository).create(SKILL_ID, RECOMMENDATION_ID);
        verify(skillOfferRepository).create(ANOTHER_SKILL_ID, RECOMMENDATION_ID);
        verifyNoMoreInteractions(recommendationRepository, skillOfferRepository, recommendationMapper);
    }

    @Test
    @DisplayName("create: throws DataValidationException when receiverId is null")
    void create_fail_receiverIdNull() {
        CreateRecommendationRequestDto input = createReq(null, CONTENT, List.of(SKILL_ID, ANOTHER_SKILL_ID));

        assertThatThrownBy(() -> recommendationService.create(input))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("receiverId is required");

        verifyNoInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
    }

    @Test
    @DisplayName("create: throws DataValidationException when content is blank")
    void create_fail_contentBlank() {
        CreateRecommendationRequestDto input
                = createReq(RECEIVER_ID, "   ", List.of(SKILL_ID, ANOTHER_SKILL_ID));

        assertThatThrownBy(() -> recommendationService.create(input))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("content must not be blank");

        verifyNoInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
    }

    @Test
    @DisplayName("create: throws DataValidationException on cooldown violations")
    void create_fail_cooldownViolation() {
        Recommendation existing = recWithCreatedAt(OLD_CONTENT, AUTHOR_ID, RECEIVER_ID, LocalDateTime.now());

        when(recommendationRepository.findAll()).thenReturn(List.of(existing));
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        CreateRecommendationRequestDto input = createReq(RECEIVER_ID, CONTENT, List.of(SKILL_ID, ANOTHER_SKILL_ID));

        assertThatThrownBy(() -> recommendationService.create(input))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("You can leave a recommendation for this user only once in");

        verify(recommendationRepository).findAll();
        verifyNoInteractions(recommendationMapper, skillOfferRepository);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    @DisplayName("create: handles skillIds if provided")
    void create_success_withSkillIds() {
        CreateRecommendationRequestDto requestDto =
                createReq(RECEIVER_ID, CONTENT, List.of(SKILL_ID, ANOTHER_SKILL_ID));

        Recommendation saved = rec(RECOMMENDATION_ID, CONTENT, AUTHOR_ID, RECEIVER_ID);

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, CONTENT)).thenReturn(RECOMMENDATION_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(saved));
        when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

        RecommendationResponseDto result = recommendationService.create(requestDto);

        assertThat(result).isEqualTo(resp(RECOMMENDATION_ID, AUTHOR_ID, RECEIVER_ID, CONTENT));

        verify(recommendationRepository).create(AUTHOR_ID, RECEIVER_ID, CONTENT);

        verify(skillOfferRepository).create(SKILL_ID, RECOMMENDATION_ID);
        verify(skillOfferRepository).create(ANOTHER_SKILL_ID, RECOMMENDATION_ID);
        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verify(recommendationRepository).findAll();
        verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
    }

    @Test
    @DisplayName("create: throws ForbiddenException when author is the same as receiver")
    void create_fail_authorEqualsReceiver() {
        CreateRecommendationRequestDto input =
                createReq(AUTHOR_ID, "Self recommendation", List.of(SKILL_ID, ANOTHER_SKILL_ID));

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        assertThatThrownBy(() -> recommendationService.create(input))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You cannot create recommendation for yourself");

        verifyNoInteractions(recommendationRepository, recommendationMapper);
    }

    @Test
    @DisplayName("update: successfully updates recommendation and returns DTO")
    void update_success() {
        Recommendation existing = rec(RECOMMENDATION_ID, OLD_CONTENT, AUTHOR_ID, RECEIVER_ID);
        Recommendation updated = rec(RECOMMENDATION_ID, UPDATED_CONTENT, AUTHOR_ID, RECEIVER_ID);

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(existing));
        when(recommendationRepository.save(existing)).thenReturn(updated);

        UpdateRecommendationRequestDto requestDto = updateReq(UPDATED_CONTENT, null);
        RecommendationResponseDto responseDto = recommendationService.update(RECOMMENDATION_ID, requestDto);

        assertThat(responseDto).isEqualTo(resp(RECOMMENDATION_ID, AUTHOR_ID, RECEIVER_ID, UPDATED_CONTENT));
        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verify(recommendationRepository).save(existing);
        verify(recommendationMapper).toResponse(updated);
        verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
    }

    @Test
    @DisplayName("update: throws DataValidationException when content is blank")
    void update_fail_contentBlank() {
        UpdateRecommendationRequestDto input = updateReq("   ", null);

        assertThatThrownBy(() -> recommendationService.update(RECOMMENDATION_ID, input))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("content must not be blank");

        verifyNoInteractions(recommendationRepository, recommendationMapper);
    }

    @Test
    @DisplayName("update: throws DataValidationException when recommendation not found")
    void update_fail_notFound() {
        UpdateRecommendationRequestDto input = updateReq(UPDATED_CONTENT, null);

        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationService.update(RECOMMENDATION_ID, input))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Recommendation not found: id=" + RECOMMENDATION_ID);

        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    @DisplayName("update: throws ForbiddenException when user tries to update another user's recommendation")
    void update_fail_forbidden() {
        Recommendation existing = rec(RECOMMENDATION_ID, OLD_CONTENT, AUTHOR_ID, RECEIVER_ID);

        when(userContext.getUserId()).thenReturn(ANOTHER_AUTHOR_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(existing));

        UpdateRecommendationRequestDto input = updateReq(UPDATED_CONTENT, null);
        assertThatThrownBy(() -> recommendationService.update(RECOMMENDATION_ID, input))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You can update only your own recommendation");

        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    @DisplayName("update: updates recommendation with skillIds and returns DTO")
    void update_success_withSkillIds() {
        Recommendation existing = rec(RECOMMENDATION_ID, OLD_CONTENT, AUTHOR_ID, RECEIVER_ID);
        Recommendation updated = rec(RECOMMENDATION_ID, UPDATED_CONTENT, AUTHOR_ID, RECEIVER_ID);

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(existing));
        when(recommendationRepository.save(existing)).thenReturn(updated);

        UpdateRecommendationRequestDto input = updateReq(UPDATED_CONTENT, List.of(SKILL_ID, ANOTHER_SKILL_ID));
        RecommendationResponseDto result = recommendationService.update(RECOMMENDATION_ID, input);

        assertThat(result).isEqualTo(resp(RECOMMENDATION_ID, AUTHOR_ID, RECEIVER_ID, UPDATED_CONTENT));
        verify(recommendationRepository).findById(RECOMMENDATION_ID);
        verify(recommendationRepository).save(existing);
        verify(skillOfferRepository).deleteAllByRecommendationId(RECOMMENDATION_ID);

        verify(skillOfferRepository).create(SKILL_ID, RECOMMENDATION_ID);
        verify(skillOfferRepository).create(ANOTHER_SKILL_ID, RECOMMENDATION_ID);
        verify(recommendationMapper).toResponse(updated);
        verifyNoMoreInteractions(recommendationRepository, recommendationMapper, skillOfferRepository);
    }

    @Test
    @DisplayName("delete: successfully deletes recommendation owned by user")
    void delete_success() {
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.deleteByIdAndAuthor_id(RECOMMENDATION_ID, AUTHOR_ID)).thenReturn(1);

        recommendationService.delete(RECOMMENDATION_ID);

        verify(recommendationRepository).deleteByIdAndAuthor_id(RECOMMENDATION_ID, AUTHOR_ID);
        verify(skillOfferRepository).deleteAllByRecommendationId(RECOMMENDATION_ID);
        verifyNoMoreInteractions(recommendationRepository, skillOfferRepository);
    }

    @Test
    @DisplayName("delete: throws ForbiddenException when user is not the owner or no recommendation")
    void delete_fail_notOwner() {
        assertNotEquals(AUTHOR_ID, RECEIVER_ID);

        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(recommendationRepository.deleteByIdAndAuthor_id(RECOMMENDATION_ID, AUTHOR_ID)).thenReturn(0);

        assertThatThrownBy(() -> recommendationService.delete(RECOMMENDATION_ID))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You can delete only your own recommendation or it does not exist");

        verify(recommendationRepository).deleteByIdAndAuthor_id(RECOMMENDATION_ID, AUTHOR_ID);
        verifyNoInteractions(skillOfferRepository);
        verifyNoMoreInteractions(recommendationRepository);
    }

    @Test
    @DisplayName("getByFilters: successfully filters recommendations based on criteria")
    void getByFilters_success() {
        Recommendation r1 = rec(RECOMMENDATION_ID, "Content with keyword", AUTHOR_ID, RECEIVER_ID);
        Recommendation r2 = rec(ANOTHER_RECOMMENDATION_ID, "Other content", AUTHOR_ID, RECEIVER_ID);

        when(recommendationRepository.findAll()).thenReturn(List.of(r1, r2));

        FilterRecommendationRequestDto f = filters("keyword", AUTHOR_ID, RECEIVER_ID);
        List<RecommendationResponseDto> response = recommendationService.getByFilters(f);

        RecommendationResponseDto dto1 = resp(
                RECOMMENDATION_ID,
                AUTHOR_ID,
                RECEIVER_ID,
                "Content with keyword");
        assertThat(response).containsExactly(dto1);

        verify(recommendationRepository).findAll();
        verify(recommendationMapper).toResponse(r1);
        verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
    }

    @Test
    @DisplayName("getByFilters: returns all recommendations when filters are empty")
    void getByFilters_emptyFilters() {
        Recommendation r1 = rec(
                RECOMMENDATION_ID,
                "First recommendation",
                AUTHOR_ID,
                RECEIVER_ID);
        Recommendation r2 = rec(
                ANOTHER_RECOMMENDATION_ID,
                "Second recommendation",
                ANOTHER_AUTHOR_ID,
                ANOTHER_RECEIVER_ID);

        when(recommendationRepository.findAll()).thenReturn(List.of(r1, r2));

        FilterRecommendationRequestDto f = filters(null, null, null);
        List<RecommendationResponseDto> response = recommendationService.getByFilters(f);

        RecommendationResponseDto dto1 = resp(
                RECOMMENDATION_ID,
                AUTHOR_ID,
                RECEIVER_ID,
                "First recommendation");
        RecommendationResponseDto dto2 = resp(
                ANOTHER_RECOMMENDATION_ID,
                ANOTHER_AUTHOR_ID,
                ANOTHER_RECEIVER_ID,
                "Second recommendation");

        assertThat(response).containsExactlyInAnyOrder(dto1, dto2);
        verify(recommendationRepository).findAll();
        verify(recommendationMapper).toResponse(r1);
        verify(recommendationMapper).toResponse(r2);
        verifyNoMoreInteractions(recommendationRepository, recommendationMapper);
    }

    @Test
    @DisplayName("getByFilters: returns empty list if no recommendations match filters")
    void getByFilters_noResults() {
        FilterRecommendationRequestDto f = filters("unmatched", 123L, 456L);

        when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

        List<RecommendationResponseDto> response = recommendationService.getByFilters(f);

        assertThat(response).isEmpty();
        verify(recommendationRepository).findAll();
        verifyNoInteractions(recommendationMapper);
        verifyNoMoreInteractions(recommendationRepository);
    }
}