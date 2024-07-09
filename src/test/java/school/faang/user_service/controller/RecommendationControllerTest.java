package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(3L)
                .receiverId(12L)
                .skillOffers(skillOfferDtoList)
                .build();

        when(service.create(recommendationDto)).thenReturn(recommendationDto);
        RecommendationDto result = controller.giveRecommendation(recommendationDto);
        verify(service).create(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void updateRecommendationTest() {
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        RecommendationDto updateRecommendationDto = RecommendationDto.builder()
                .id(2L)
                .authorId(3L)
                .receiverId(14L)
                .skillOffers(skillOfferDtoList)
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
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        List<RecommendationDto> recommendationDtos = List.of(
                RecommendationDto.builder()
                        .id(1L)
                        .authorId(3L)
                        .receiverId(receiverId)
                        .skillOffers(skillOfferDtoList)
                        .build());

        when(service.getAllUserRecommendations(receiverId)).thenReturn(recommendationDtos);
        List<RecommendationDto> result = controller.getAllUserRecommendations(receiverId);
        verify(service).getAllUserRecommendations(receiverId);
        assertNotNull(result);
        assertEquals(recommendationDtos, result);
    }

    @Test
    void getAllGivenRecommendationsTest() {
        long authorId = 1L;
        List<SkillOfferDto> skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        List<RecommendationDto> recommendationDtos = List.of(
                RecommendationDto.builder()
                        .id(1L)
                        .authorId(authorId)
                        .receiverId(14L)
                        .skillOffers(skillOfferDtoList)
                        .build());

        when(service.getAllUserRecommendations(authorId)).thenReturn(recommendationDtos);
        List<RecommendationDto> result = controller.getAllUserRecommendations(authorId);
        verify(service).getAllUserRecommendations(authorId);
        assertNotNull(result);
        assertEquals(recommendationDtos, result);
    }

    @Test
    void deleteRecommendationTest() {
        controller.deleteRecommendation(anyLong());
        verify(service).delete(anyLong());
    }
}
