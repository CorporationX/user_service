package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
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

    private long authorId;
    private long receiverId;
    private List<SkillOfferDto> skillOfferDtoList;
    private RecommendationDto recommendationDto;
    private List<RecommendationDto> recommendationDtos;

    @BeforeEach
    void init() {
        authorId = 3L;
        receiverId = 12L;
        skillOfferDtoList = List.of(SkillOfferDto.builder()
                .id(2L)
                .skillId(1L).build());
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(authorId)
                .receiverId(receiverId)
                .skillOffers(skillOfferDtoList)
                .build();

        recommendationDtos = List.of(recommendationDto);
    }


    @Test
    void giveRecommendationTest() {
        when(service.create(recommendationDto)).thenReturn(recommendationDto);
        RecommendationDto result = controller.giveRecommendation(recommendationDto);
        verify(service).create(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void updateRecommendationTest() {
        when(service.update(recommendationDto)).thenReturn(recommendationDto);
        RecommendationDto result = controller.updateRecommendation(recommendationDto);
        verify(service).update(recommendationDto);
        assertNotNull(result);
        assertEquals(recommendationDto, result);
    }

    @Test
    void getAllUserRecommendationTest() {
        when(service.getAllUserRecommendations(receiverId)).thenReturn(recommendationDtos);
        List<RecommendationDto> result = controller.getAllUserRecommendations(receiverId);
        verify(service).getAllUserRecommendations(receiverId);
        assertNotNull(result);
        assertEquals(recommendationDtos, result);
    }

    @Test
    void getAllGivenRecommendationsTest() {
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
