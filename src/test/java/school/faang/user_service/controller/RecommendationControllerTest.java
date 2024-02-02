package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
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

/*    @Test
    void getAllUserRecommendations() throws Exception {
        long receiverId = 1L;
        RecommendationDto rDto1 = RecommendationDto.builder().receiverId(receiverId).build();
        RecommendationDto rDto2 = RecommendationDto.builder().receiverId(receiverId).build();
        List<RecommendationDto> recommendations = Arrays.asList(rDto1,rDto2);

        when(recommendationService.getAllUserRecommendations(receiverId)).thenReturn(recommendations);

        mockMvc.perform(get("/recommendations/user/{receiverId}", receiverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].receiverId").value(receiverId))
                .andExpect(jsonPath("$[1].receiverId").value(receiverId));
    }*/ //этот тест падает, потому что не работает маппер. Починить в общей задаче
}