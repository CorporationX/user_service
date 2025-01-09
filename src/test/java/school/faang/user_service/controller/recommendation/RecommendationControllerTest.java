package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.event.RecommendationEvent;
import school.faang.user_service.publisher.RecommendationEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Qualifier;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecommendationControllerIntegration2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationEventPublisher recommendationEventPublisher;

    @Autowired
    @Qualifier("redisObjectMapper")
    private ObjectMapper objectMapper;

    @Test
    void testPublishRecommendation() throws Exception {
        RecommendationEvent event = new RecommendationEvent(1L, 2L, 3L, LocalDateTime.now());

        // Mock the new method in RecommendationEventPublisher
        doNothing().when(recommendationEventPublisher).publishToRecommendation(any(RecommendationEvent.class));

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());

        // Verify that the new method is called
        verify(recommendationEventPublisher, times(1)).publishToRecommendation(any(RecommendationEvent.class));
    }
}
