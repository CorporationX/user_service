package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private RecommendationDto recommendationDto;
    private long id;
    @BeforeEach
    void setup() {
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .skillOffers(List.of(new SkillOfferDto(2L, 1L)))
                .authorId(5L)
                .receiverId(4L)
                .content("some content")
                .createdAt(LocalDateTime.now())
                .build();

        id = 1L;
    }

    @Test
    void testGiveRecommendationOk() {
        recommendationController.giveRecommendation(recommendationDto);

        verify(recommendationService).create(recommendationDto);
    }

    @Test
    void testUpdateRecommendationOk(){
        recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationService).update(recommendationDto);
    }

    @Test
    void testDeleteRecommendationOk(){
        recommendationController.deleteRecommendation(id);

        verify(recommendationService).delete(id);
    }

    @Test
    void testGetAllUserRecommendationsOk(){
        recommendationController.getAllUserRecommendations(id);

        verify(recommendationService).getAllUserRecommendations(id);
    }

    @Test
    void testGetAllGivenRecommendations(){
        recommendationController.getAllGivenRecommendations(id);

        verify(recommendationService).getAllGivenRecommendations(id);
    }
}
