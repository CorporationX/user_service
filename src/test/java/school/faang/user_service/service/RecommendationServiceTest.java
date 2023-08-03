package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Spy
    private RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testGetAllUserRecommendations() {
        Long receiverId = 2L;
        LocalDateTime createdAt = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 5);

        User receiver = new User();
        receiver.setId(receiverId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setAuthor(User.builder().id(1L).build());
        recommendation1.setReceiver(receiver);
        recommendation1.setCreatedAt(createdAt);
        recommendation1.setUpdatedAt(createdAt);

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setAuthor(User.builder().id(4L).build());
        recommendation2.setReceiver(receiver);
        recommendation2.setCreatedAt(createdAt);
        recommendation2.setUpdatedAt(createdAt);

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto(1L, 1L, receiverId, "recommendation 1", null, createdAt);
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, 4L, receiverId, "recommendation 2", null, createdAt);
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByReceiverId(receiverId, pageable)).thenReturn(recommendationPage);

        Page<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId, pageable.getPageNumber(), pageable.getPageSize());

        verify(recommendationRepository).findAllByReceiverId(receiverId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage.getContent(), result.getContent());
    }
    @Test
    public void testGetAllGivenRecommendations() {
        Long authorId = 2L;
        LocalDateTime createdAt = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 5);

        User author = new User();
        author.setId(authorId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        recommendation1.setContent("recommendation 1");
        recommendation1.setAuthor(author);
        recommendation1.setReceiver(User.builder().id(1L).build());
        recommendation1.setCreatedAt(createdAt);
        recommendation1.setUpdatedAt(createdAt);

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        recommendation2.setContent("recommendation 2");
        recommendation2.setAuthor(author);
        recommendation2.setReceiver(User.builder().id(4L).build());
        recommendation2.setCreatedAt(createdAt);
        recommendation2.setUpdatedAt(createdAt);

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendation1);
        recommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtoList = new ArrayList<>();

        RecommendationDto recommendationDto1 = new RecommendationDto(1L, authorId, 1L, "recommendation 1", null, createdAt);
        RecommendationDto recommendationDto2 = new RecommendationDto(2L, authorId, 4L, "recommendation 2", null, createdAt);
        recommendationDtoList.add(recommendationDto1);
        recommendationDtoList.add(recommendationDto2);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        Page<RecommendationDto> recommendationDtoPage = new PageImpl<>(recommendationDtoList);

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationPage);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId, pageable.getPageNumber(), pageable.getPageSize());

        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper).toDto(recommendation1);
        verify(recommendationMapper).toDto(recommendation2);
        assertEquals(2, recommendationDtoList.size());
        assertEquals(recommendationDtoPage.getContent(), result.getContent());
    }
}