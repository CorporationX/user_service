package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class TestRecommendationController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationService recommendationService;

    @Test
    void testGiveRecommendation() throws Exception {
        RecommendationDto recommendationDto = new RecommendationDto();
        // Populate the DTO with data

        when(recommendationService.create(any(RecommendationDto.class)))
                .thenReturn(recommendationDto);

        mockMvc.perform((org.springframework.test.web.servlet.RequestBuilder) post("/recommendations")
                        .contentType(MediaType.valueOf("application/json"))
                        .contentType(MediaType.valueOf("{...}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test Recommendation"));
    }
}
