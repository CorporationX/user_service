package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.RecommendationAuthorFilterInstance;
import school.faang.user_service.filter.RecommendationContentFilterInstance;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.filter.RecommendationReceiverFilterInstance;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestedEventPublisher;
import school.faang.user_service.service.recommendation.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    private RecommendationServiceImpl recommendationService;

    @Mock
    private RecommendationRequestedEventPublisher eventPublisher;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    private final int repeatRecommendationTimeLimit = 6;

    private final RecommendationFilter authorFilter = new RecommendationAuthorFilterInstance();
    private final RecommendationFilter contentFilter = new RecommendationContentFilterInstance();
    private final RecommendationFilter receiverFilter = new RecommendationReceiverFilterInstance();

    @Captor
    private ArgumentCaptor<Recommendation> recommendationCaptor;
    @Captor
    private ArgumentCaptor<Long> authorIdCaptor;
    @Captor
    private ArgumentCaptor<Long> recommendationIdCaptor;

    @BeforeEach
    public void setUp() {
        recommendationService = new RecommendationServiceImpl(
                recommendationRepository,
                recommendationMapper,
                userContext,
                6,
                List.of(authorFilter, contentFilter, receiverFilter),
                eventPublisher
        );
    }

    @Test
    public void testUserSelfRecommend() {
        CreateRecommendationDto dto = buildCreateDto(3L, "some content");
        when(userContext.getUserId()).thenReturn(dto.receiverId());

        assertThrows(ForbiddenException.class,
                () -> recommendationService.create(dto));
    }

    @Test
    public void testLatestRecommendationCheck() {
        CreateRecommendationDto dto = buildCreateDto(3L, "some content");
        User author = buildUser(1L);
        User receiver = buildUser(dto.receiverId());

        when(userContext.getUserId()).thenReturn(author.getId());
        when(recommendationRepository.findAll()).thenReturn(List.of(
                buildRecommendation(5L, author, receiver,
                        LocalDateTime.now().minusMonths(repeatRecommendationTimeLimit - 2))
        ));

        assertThrows(ForbiddenException.class,
                () -> recommendationService.create(dto));
    }

    @Test
    public void testRecommendationCreation() {
        CreateRecommendationDto dto = buildCreateDto(3L, "some content");
        long newId = 10L;
        User author = buildUser(1L);
        User receiver = buildUser(dto.receiverId());

        when(userContext.getUserId()).thenReturn(author.getId());
        when(recommendationRepository.findAll()).thenReturn(List.of(
                buildRecommendation(5L, author, receiver,
                        LocalDateTime.now().minusMonths(repeatRecommendationTimeLimit + 2))
        ));
        when(recommendationRepository.create(anyLong(), anyLong(), anyString()))
                .thenReturn(newId);
        when(recommendationRepository.findById(newId)).thenReturn(Optional.of(
                Recommendation.builder()
                        .id(newId)
                        .author(author)
                        .receiver(receiver)
                        .content(dto.content())
                        .build()
        ));

        recommendationService.create(dto);

        verify(userContext, atLeastOnce()).getUserId();
        verify(recommendationRepository).create(author.getId(), receiver.getId(), dto.content());
    }

    @Test
    public void testRecommendationUpdatePermissions() {
        long id = 1L;
        long authorId = 3L;

        when(userContext.getUserId()).thenReturn(2L);
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(
                Recommendation.builder()
                        .id(id)
                        .author(buildUser(authorId))
                        .content("some content")
                        .build()
        ));

        assertThrows(ForbiddenException.class,
                () -> recommendationService.update(id,
                        UpdateRecommendationDto.builder().content("updated content").build()));
    }

    @Test
    public void testRecommendationUpdate() {
        long id = 1L;
        long authorId = 2L;
        User author = buildUser(authorId);

        UpdateRecommendationDto updateDto = UpdateRecommendationDto.builder()
                .content("updated content")
                .build();

        when(userContext.getUserId()).thenReturn(authorId);
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(
                Recommendation.builder()
                        .id(id)
                        .author(author)
                        .build()
        ));

        recommendationService.update(id, updateDto);

        verify(recommendationRepository).save(recommendationCaptor.capture());
        Recommendation captured = recommendationCaptor.getValue();
        assertEquals(id, captured.getId());
        assertEquals(author, captured.getAuthor());
    }

    @Test
    public void testRecommendationDeletePermissions() {
        long id = 1L;
        when(userContext.getUserId()).thenReturn(2L);
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(
                Recommendation.builder()
                        .id(id)
                        .author(buildUser(3L))
                        .build()
        ));

        assertThrows(ForbiddenException.class,
                () -> recommendationService.delete(id));
    }

    @Test
    public void testRecommendationDelete() {
        long id = 1L;
        long authorId = 2L;

        when(userContext.getUserId()).thenReturn(authorId);
        when(recommendationRepository.findById(id)).thenReturn(Optional.of(
                Recommendation.builder()
                        .id(id)
                        .author(buildUser(authorId))
                        .build()
        ));

        recommendationService.delete(id);

        verify(recommendationRepository).deleteByIdAndAuthor_id(
                recommendationIdCaptor.capture(), authorIdCaptor.capture());

        assertEquals(id, recommendationIdCaptor.getValue());
        assertEquals(authorId, authorIdCaptor.getValue());
    }

    @Test
    public void testRecommendationNoMatch() {
        Recommendation rec1 = buildRecommendation(1L, buildUser(1L), buildUser(2L), "Java");
        Recommendation rec2 = buildRecommendation(2L, buildUser(1L), buildUser(3L), "Python");

        when(recommendationRepository.findAll()).thenReturn(List.of(rec1, rec2));

        List<RecommendationDto> result = recommendationService.getByFilters(
                new RecommendationFilterDto(null, null, null));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testRecommendationAuthorFiltering() {
        Recommendation firstRecommendation =
                buildRecommendation(1L, buildUser(1L), buildUser(2L), "first Recommendation Java");

        Recommendation secondRecommendation =
                buildRecommendation(2L, buildUser(1L), buildUser(3L), "second Recommendation Python");

        Recommendation thirdRecommendation =
                buildRecommendation(3L, buildUser(4L), buildUser(3L), "third Recommendation Java");

        when(recommendationRepository.findAll()).thenReturn(List.of(
                firstRecommendation,
                secondRecommendation,
                thirdRecommendation));

        List<RecommendationDto> resultResponse = recommendationService.getByFilters(
                new RecommendationFilterDto(null, null, null));

        resultResponse.forEach(System.out::println);
        assertEquals(1, resultResponse.size());
        assertEquals("third Recommendation Java",
                resultResponse.get(0).content());
        assertEquals(3L, resultResponse.get(0).receiverId());
    }

    private CreateRecommendationDto buildCreateDto(long receiverId, String content) {
        return CreateRecommendationDto.builder()
                .receiverId(receiverId)
                .content(content)
                .build();
    }

    private User buildUser(long id) {
        return User.builder()
                .id(id)
                .build();
    }

    private Recommendation buildRecommendation(long id, User author, User receiver, String content) {
        return Recommendation.builder()
                .id(id)
                .author(author)
                .receiver(receiver)
                .content(content)
                .build();
    }

    private Recommendation buildRecommendation(long id, User author, User receiver, LocalDateTime createdAt) {
        return Recommendation.builder()
                .id(id)
                .author(author)
                .receiver(receiver)
                .createdAt(createdAt)
                .build();
    }
}
