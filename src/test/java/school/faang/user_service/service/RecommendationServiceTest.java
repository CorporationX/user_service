package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void createTest() {
        long authorId = 1L;
        long receiverId = 2L;
        String content = "some content";
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .receiverId(receiverId)
                .authorId(authorId)
                .content(content)
                .skillOffers(skillOfferDtoList)
                .build();
        Recommendation recommendation =
                Recommendation.builder()
                        .id(1l)
                        .createdAt(LocalDateTime.now())
                        .content(content)
                        .author(User.builder()
                                .id(authorId).build())
                        .receiver(User.builder()
                                .id(receiverId)
                                .recommendationsReceived(List.of(new Recommendation())).build())
                        .skillOffers(List.of(new SkillOffer()))
                        .build();

        doNothing().when(recommendationValidator).validateRecommendationDto(recommendationDto);
        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.toDto(any())).thenReturn(recommendationDto);
        RecommendationDto result = recommendationService.create(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void updateTest() {
        long authorId = 4L;
        long receiverId = 2L;
        String content = "some content";
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(3L)
                .skillId(4L).build());
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .receiverId(receiverId)
                .authorId(authorId)
                .content(content)
                .skillOffers(skillOfferDtoList)
                .build();
        Recommendation recommendation =
                Recommendation.builder()
                        .id(1l)
                        .createdAt(LocalDateTime.now())
                        .content(content)
                        .author(User.builder()
                                .id(authorId).build())
                        .receiver(User.builder()
                                .id(receiverId)
                                .recommendationsReceived(List.of(new Recommendation())).build())
                        .skillOffers(List.of(new SkillOffer()))
                        .build();

        when(recommendationRepository.existsById(recommendationDto.getId())).thenReturn(true);
        doNothing().when(recommendationValidator).validateRecommendationDto(recommendationDto);
        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.toDto(any())).thenReturn(recommendationDto);
        RecommendationDto result = recommendationService.update(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void getAllUserRecommendations() {
        long authorId = 1L;
        long recommendationId = 2L;
        List<RecommendationDto> recommendationsDto = List.of(
                RecommendationDto.builder()
                        .id(recommendationId)
                        .authorId(authorId)
                        .build());

        when(recommendationMapper.recommendationToRecommendationDto(any())).thenReturn(recommendationsDto);
        doNothing().when(recommendationValidator).checkRecommendationList(any(), anyLong());
        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(authorId);
        assertNotNull(result);
        assertEquals(recommendationsDto, result);
    }

    @Test
    void getAllGivenRecommendations() {
        long receiverId = 1L;
        long recommendationId = 2L;
        List<RecommendationDto> recommendationsDto = List.of(
                RecommendationDto.builder()
                        .id(recommendationId)
                        .authorId(receiverId)
                        .build());

        when(recommendationMapper.recommendationToRecommendationDto(any())).thenReturn(recommendationsDto);
        doNothing().when(recommendationValidator).checkRecommendationList(any(), anyLong());
        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(receiverId);
        assertNotNull(result);
        assertEquals(recommendationsDto, result);
    }

    @Test
    void delete() {
        long recommendationId = 1L;
        when(recommendationRepository.existsById(recommendationId)).thenReturn(true);
        recommendationService.delete(recommendationId);
        verify(recommendationValidator).validateId(recommendationId);
        verify(recommendationRepository).existsById(recommendationId);
    }

    @Test
    void deleteInvalidId() {
        long recommendationId = 0L;
        assertThrows(IllegalArgumentException.class, () -> recommendationService.delete(recommendationId));
        verify(recommendationValidator).validateId(recommendationId);
    }

    @Test
    void deleteNotExistById() {
        long recommendationId = 10000L;
        when(recommendationRepository.existsById(recommendationId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> recommendationService.delete(recommendationId));
        verify(recommendationValidator).validateId(recommendationId);
        verify(recommendationRepository).existsById(recommendationId);
    }
}
