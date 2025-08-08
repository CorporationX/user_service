package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.RecommendationReceivedEventDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.recommendation.AnotherAuthorException;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl();

    @Mock
    private RecommendationValidator recommendationValidator;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationFilter filter1;

    @Mock
    private RecommendationFilter filter2;

    @Captor
    private ArgumentCaptor<RecommendationReceivedEventDto> recommendationReceivedEventCaptor;

    @Test
    void createDoesNotSaveIfValidationException() {
        long receiverId = 2L;
        String content = "Hey Jude!";
        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, content);
        doThrow(new AnotherAuthorException("error")).when(recommendationValidator).validateCreate(createDto);

        assertThrows(AnotherAuthorException.class, () -> recommendationService.create(createDto));

        verify(recommendationRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createSavesRecommendationAndPublishesEventIfNoValidationException() {
        long userId = 1L;
        User author = new User();
        author.setId(userId);
        long receiverId = 2L;
        User receiver = new User();
        receiver.setId(receiverId);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(author);
        when(userRepository.getByIdOrThrow(receiverId)).thenReturn(receiver);
        when(userContext.getUserId()).thenReturn(userId);
        String content = "Hey Jude!";
        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, content);
        doNothing().when(recommendationValidator).validateCreate(createDto);
        Recommendation resultRecommendation = Recommendation.builder()
                .author(author)
                .receiver(receiver)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        when(recommendationRepository.save(Mockito.any())).thenReturn(resultRecommendation);

        RecommendationDto returnResult = recommendationService.create(createDto);

        assertEquals(resultRecommendation.getReceiver().getId(), returnResult.receiverId());
        assertEquals(resultRecommendation.getAuthor().getId(), returnResult.authorId());
        assertEquals(resultRecommendation.getContent(), returnResult.content());
        verify(recommendationRepository, Mockito.times(1)).save(Mockito.any());
        verify(recommendationReceivedEventPublisher, Mockito.times(1))
                .publish(recommendationReceivedEventCaptor.capture());
        RecommendationReceivedEventDto capturedEventDto = recommendationReceivedEventCaptor.getValue();
        assertEquals(receiverId, capturedEventDto.receiverId());
    }

    @Test
    void updateThrowsIfNonExistentRecommendationId() {
        long recommendationId = 1;
        UpdateRecommendationDto updateDto = new UpdateRecommendationDto("New content");
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.update(recommendationId, updateDto));
    }

    @Test
    void updateDoesNotUpdateIfValidationException() {
        long userId = 1;
        long authorId = 2;
        long recommendationId = 1;
        Recommendation recommendation = getRecommendation(recommendationId, authorId, userId, "GO!");
        UpdateRecommendationDto updateDto = new UpdateRecommendationDto("Update!");
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
        doThrow(new AnotherAuthorException("error")).when(recommendationValidator).validateUpdate(recommendation);

        assertThrows(AnotherAuthorException.class, () -> recommendationService.update(recommendationId, updateDto));

        verify(recommendationRepository, Mockito.never()).update(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any()
        );
    }

    @Test
    void updateShouldUpdateWithDtoAndSave() {
        long userId = 1;
        long recommendationId = 1;
        String originalContent = "Old me.";
        String newContent = "Check me!";
        Recommendation recommendation = getRecommendation(recommendationId, userId, 2, originalContent);
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
        doNothing().when(recommendationValidator).validateUpdate(recommendation);
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
        UpdateRecommendationDto updateDto = new UpdateRecommendationDto(newContent);

        RecommendationDto resultDto = recommendationService.update(recommendationId, updateDto);

        verify(recommendationMapper, Mockito.times(1)).update(updateDto, recommendation);
        verify(recommendationRepository, Mockito.times(1)).save(recommendation);
        assertEquals(newContent, resultDto.content());
    }

    @Test
    void deleteThrowsIfNonExistentRecommendationId() {
        long recommendationId = 1;
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.delete(recommendationId));
    }

    @Test
    void deleteDoesNotDeleteIfValidationException() {
        long receiverId = 1;
        long authorId = 2;
        long recommendationId = 1;
        Recommendation recommendation = getRecommendation(recommendationId, authorId, receiverId, "Some");
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
        doThrow(new RuntimeException("error")).when(recommendationValidator).validateDelete(recommendation);

        assertThrows(RuntimeException.class, () -> recommendationService.delete(recommendationId));
        verify(recommendationRepository, Mockito.never()).deleteByIdAndAuthor_id(
                recommendationId,
                authorId
        );
    }

    @Test
    void deleteDeletesIfNoValidationException() {
        long recommendationId = 1;
        Recommendation recommendation = getRecommendation(recommendationId, 1L, 2L, "Delete me");
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
        doNothing().when(recommendationValidator).validateDelete(recommendation);

        recommendationService.delete(recommendationId);

        verify(recommendationRepository, Mockito.times(1))
                .deleteByIdAndAuthor_id(recommendationId, recommendation.getAuthor().getId());
    }

    @Test
    void getByFiltersReturnsAllIfNoFilters() {
        List<Recommendation> allRecommendations = getListForFiltering();
        List<RecommendationFilter> filters = List.of(filter1, filter2);
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
        when(recommendationRepository.findAll()).thenReturn(allRecommendations);
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);

        List<RecommendationDto> matchedRecommendations = recommendationService.getByFilters(filterDto);

        assertEquals(matchedRecommendations.size(), allRecommendations.size());
        assertEquals(matchedRecommendations.get(0).content(), allRecommendations.get(0).getContent());
        assertEquals(matchedRecommendations.get(1).content(), allRecommendations.get(1).getContent());
    }

    @Test
    void getByFiltersChecksApplicableFiltersAndApplies() {
        List<Recommendation> allRecommendations = getListForFiltering();
        List<RecommendationFilter> filters = List.of(filter1, filter2);
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
        when(recommendationRepository.findAll()).thenReturn(allRecommendations);
        when(filter1.isApplicable(Mockito.any())).thenReturn(true);
        when(filter2.isApplicable(Mockito.any())).thenReturn(true);
        when(filter1.apply(Mockito.any(), Mockito.any())).thenReturn(allRecommendations.stream());
        when(filter2.apply(Mockito.any(), Mockito.any())).thenReturn(allRecommendations.stream());
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);

        recommendationService.getByFilters(filterDto);

        verify(filter1, Mockito.times(1)).isApplicable(Mockito.any());
        verify(filter2, Mockito.times(1)).isApplicable(Mockito.any());
        verify(filter1, Mockito.times(1)).apply(Mockito.any(), Mockito.any());
        verify(filter2, Mockito.times(1)).apply(Mockito.any(), Mockito.any());
    }

    @Test
    void getByFiltersFiltersOutResults() {
        List<Recommendation> allRecommendations = getListForFiltering();
        List<RecommendationFilter> filters = List.of(filter1);
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
        when(recommendationRepository.findAll()).thenReturn(allRecommendations);
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);
        when(filter1.isApplicable(Mockito.any())).thenReturn(true);
        when(
                filter1.apply(Mockito.any(), Mockito.any()))
                .thenAnswer((Answer<Stream<Recommendation>>) invocation -> {
                    Stream<Recommendation> recommendationStream = invocation.getArgument(0);
                    return recommendationStream.filter(r -> false);
                });

        List<RecommendationDto> matchedRecommendations = recommendationService.getByFilters(filterDto);

        verify(filter1, Mockito.times(1)).apply(Mockito.any(), Mockito.any());
        assertEquals(0, matchedRecommendations.size());
    }

    @Test
    void getByFiltersSkipsFiltersThatAreNotApplicable() {
        List<Recommendation> allRecommendations = getListForFiltering();
        List<RecommendationFilter> filters = List.of(filter1, filter2);
        ReflectionTestUtils.setField(recommendationService, "recommendationFilters", filters);
        when(recommendationRepository.findAll()).thenReturn(allRecommendations);
        when(filter1.isApplicable(Mockito.any())).thenReturn(false);
        when(filter2.isApplicable(Mockito.any())).thenReturn(true);
        when(filter2.apply(Mockito.any(), Mockito.any())).thenReturn(allRecommendations.stream());

        recommendationService.getByFilters(new RecommendationFilterDto(null, null, null));

        verify(filter1, Mockito.times(1)).isApplicable(Mockito.any());
        verify(filter1, Mockito.never()).apply(Mockito.any(), Mockito.any());
        verify(filter2, Mockito.times(1)).apply(Mockito.any(), Mockito.any());
    }

    private Recommendation getRecommendation(long recommendationId, long authorId, long receiverId, String content) {
        User author = new User();
        author.setId(authorId);
        User receiver = new User();
        receiver.setId(receiverId);
        return Recommendation.builder().id(recommendationId).author(author).receiver(receiver).content(content).build();
    }

    private List<Recommendation> getListForFiltering() {
        String content1 = "Hey 1!";
        String content2 = "Hey 2!";
        Recommendation r1 = getRecommendation(1L, 1L, 2L, content1);
        Recommendation r2 = getRecommendation(2L, 2L, 1L, content2);
        return List.of(r1, r2);
    }
}