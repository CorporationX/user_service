package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.model.dto.RecommendationDto;
import school.faang.user_service.service.impl.RecommendationServiceImpl;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RecommendationController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UserHeaderFilter.class)
})
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationServiceImpl recommendationService;

    @Test
    void giveRecommendation_ReturnsRecommendationDto_WhenContentIsValid() throws Exception {
        RecommendationDto dto = new RecommendationDto();
        dto.setContent("Valid content");
        when(recommendationService.create(any(RecommendationDto.class))).thenReturn(dto);

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Valid content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isString())
                .andExpect(jsonPath("$.content").value("Valid content"));

        verify(recommendationService).create(any(RecommendationDto.class));
    }

    @Test
    void updateRecommendation_UpdatesAndReturnsDto_WhenValidRequest() throws Exception {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setContent("Updated content");

        when(recommendationService.update(anyLong(), any(RecommendationDto.class))).thenReturn(recommendationDto);

        mockMvc.perform(put("/recommendations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"content\":\"Updated content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Updated content"));

        verify(recommendationService).update(anyLong(), any(RecommendationDto.class));
    }

    @Test
    void testGetAllUserRecommendations_ReturnsListOfRecommendations_WhenCalledWithValidUserId() throws Exception {
        RecommendationDto firstRecommendationDto = new RecommendationDto();
        firstRecommendationDto.setId(1L);
        firstRecommendationDto.setContent("Content for user 1");

        RecommendationDto secondRecommendationDto = new RecommendationDto();
        secondRecommendationDto.setId(2L);
        secondRecommendationDto.setContent("Content for user 2");

        List<RecommendationDto> recommendationList = List.of(firstRecommendationDto, secondRecommendationDto);
        when(recommendationService.getAllUserRecommendations(1L)).thenReturn(recommendationList);

        mockMvc.perform(get("/recommendations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Content for user 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].content").value("Content for user 2"));

        verify(recommendationService).getAllUserRecommendations(1L);
    }

    @Test
    void testDeleteRecommendation_DeletesEntity_WhenCalled() throws Exception {
        mockMvc.perform(delete("/recommendations/1"))
                .andExpect(status().isOk());

        verify(recommendationService).delete(1L);
    }
}
