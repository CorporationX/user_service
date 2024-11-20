package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        RecommendationController.class,
        RecommendationService.class
})
public class RecommendationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    RecommendationService recommendationService;

    private final static String BASE_URL = "/recommendation";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void positiveGiveRecommendation() throws Exception {
        RecommendationDto request = getValidRequestDto();
        RecommendationDto response = getValidResponseDto();

        when(recommendationService.create(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(response)))
                .andExpect(status().isCreated());
    }

    @Test
    public void positiveUpdateRecommendation() throws Exception {
        RecommendationDto request = getValidRequestDto();
        RecommendationDto response = getValidResponseDto();

        when(recommendationService.update(request)).thenReturn(response);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(response)))
                .andExpect(status().isOk());
    }

    @Test
    public void positiveDeleteRecommendation() throws Exception {
        long id = 1L;
        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    public void positiveGetAllUserRecommendations() throws Exception {
        long id = 1L;
        int page = 1;
        int size = 1;
        List<RecommendationDto> response = List.of(getValidResponseDto());

        when(recommendationService.getAllUserRecommendations(id, page, size)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/receiver/{id}", id)
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(response)))
                .andExpect(status().isOk());
    }
    @Test
    public void positiveGetAllGivenRecommendations() throws Exception {
        long id = 1L;
        int page = 1;
        int size = 1;
        List<RecommendationDto> response = List.of(getValidResponseDto());

        when(recommendationService.getAllGivenRecommendations(id, page, size)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/author/{id}", id)
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(response)))
                .andExpect(status().isOk());
    }

    private RecommendationDto getValidRequestDto() {
        return RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .build();
    }

    private RecommendationDto getValidResponseDto() {
        return RecommendationDto.builder()
                .id(42L)
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .build();
    }
}
