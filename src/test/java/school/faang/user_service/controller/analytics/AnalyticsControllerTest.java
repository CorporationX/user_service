package school.faang.user_service.controller.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;
import school.faang.user_service.service.analytics.ProfileVisitService;
import school.faang.user_service.service.analytics.SearchAppearanceService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
public class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objMapper;
    @MockBean
    private UserContext userContext;
    @MockBean
    private ProfileVisitService visitService;

    @MockBean
    private SearchAppearanceService searchAppearanceService;

    @Test
    @DisplayName("успешное получение списка посетителей профиля")
    public void getProfileVisitsSuccess() throws Exception {
        var now = LocalDateTime.now();
        var visitedId = 1L;
        var visitor = new ProfileVisitViewDto(1L, 2L, visitedId, now.minusHours(1));
        var visitor2 = new ProfileVisitViewDto(1L, 3L, visitedId, now);
        var visitors = List.of(visitor, visitor2);
        when(visitService.getUserVisitors(visitedId, 20, 0))
                .thenReturn(visitors);
        mockMvc.perform(get("/analytics/users/" + visitedId + "/visits"))
                .andExpect(content().json(objMapper.writeValueAsString(visitors)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("успешное получение списка поисковых появлений пользователя")
    public void getSearchAppearanceSuccess() throws Exception {
        var now = LocalDateTime.now();
        var searchedId = 1L;
        var searcher1 = new SearchAppearanceViewDto(1L, 2L, searchedId, now.minusHours(1));
        var searcher2 = new SearchAppearanceViewDto(1L, 3L, searchedId, now);
        var searchers = List.of(searcher1, searcher2);
        when(searchAppearanceService.getUserSearchAppearance(searchedId, 20, 0))
                .thenReturn(searchers);
        mockMvc.perform(get("/analytics/users/" + searchedId + "/search-appearances"))
                .andExpect(content().json(objMapper.writeValueAsString(searchers)))
                .andExpect(status().isOk());
    }
}
