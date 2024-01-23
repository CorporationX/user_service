package school.faang.user_service.controller;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;
  
  
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController).build();
    }

    @Test
    public void testMethod() {
    recommendationController.getAllGivenRecommendations(1L);
        verify(recommendationService, times(1)).getAllGivenRecommendations(1L);
    }

    @Test
    void getAllUserRecommendations() throws Exception {
        long receiverId = 1L;
        List<RecommendationDto> recommendations = Arrays.asList(
                new RecommendationDto(receiverId),
                new RecommendationDto(receiverId)
        );

        when(recommendationService.getAllUserRecommendations(receiverId)).thenReturn(recommendations);

        mockMvc.perform(get("/recommendations/user/{receiverId}", receiverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(recommendations.size()))
                .andExpect(jsonPath("$[0].receiverId").value(receiverId))
                .andExpect(jsonPath("$[1].receiverId").value(receiverId));
    }
}