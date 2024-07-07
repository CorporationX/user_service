package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @InjectMocks
    private RecommendationController controller;
    @Mock
    private RecommendationService service;


    @Test
    void giveRecommendationTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(3L)
                .receiverId(12L)
                .skillOffers(List.of(new SkillOfferDto()))
                .build();

        when(service.create(recommendationDto)).thenReturn(recommendationDto);
        RecommendationDto result = controller.giveRecommendation(recommendationDto);
        verify(service).create(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void updateRecommendationTest() {
        RecommendationDto updateRecommendationDto = RecommendationDto.builder()
                .id(2L)
                .authorId(3L)
                .receiverId(14L)
                .skillOffers(List.of(new SkillOfferDto()))
                .build();

        when(service.update(updateRecommendationDto)).thenReturn(updateRecommendationDto);
        RecommendationDto result = controller.updateRecommendation(updateRecommendationDto);
        verify(service).update(updateRecommendationDto);
        assertNotNull(result);
        assertEquals(updateRecommendationDto, result);
    }

    @Test
    void getAllUserRecommendationTest() {
        long receiverId = 1L;
        Pageable pageable = PageRequest.ofSize(10);
        List<RecommendationDto> recommendationDtos = List.of(
                RecommendationDto.builder()
                        .id(1L)
                        .authorId(3L)
                        .receiverId(receiverId)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build());

        when(service.getAllUserRecommendations(receiverId, pageable)).thenReturn(recommendationDtos);
        List<RecommendationDto> result = controller.getAllUserRecommendations(receiverId, pageable);
        verify(service).getAllUserRecommendations(receiverId, pageable);
        assertNotNull(result);
        assertEquals(recommendationDtos, result);
    }

    @Test
    void getAllGivenRecommendationsTest() {
        long authorId = 1L;
        Pageable pageable = PageRequest.ofSize(10);
        List<RecommendationDto> recommendationDtos = List.of(
                RecommendationDto.builder()
                        .id(1L)
                        .authorId(authorId)
                        .receiverId(14L)
                        .skillOffers(List.of(new SkillOfferDto()))
                        .build());

        when(service.getAllUserRecommendations(authorId, pageable)).thenReturn(recommendationDtos);
        List<RecommendationDto> result = controller.getAllUserRecommendations(authorId, pageable);
        verify(service).getAllUserRecommendations(authorId, pageable);
        assertNotNull(result);
        assertEquals(recommendationDtos, result);
    }

    @Test
    void deleteRecommendationTest() {
        controller.deleteRecommendation(anyLong());
        verify(service).delete(anyLong());
    }
}
