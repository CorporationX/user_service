package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.career.CareerController;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.service.career.CareerService;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CareerController.class)
class CareerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CareerService careerService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private final long userId = 1L;
    private final long careerId = 100L;

    private CareerViewDto viewDto;

    @BeforeEach
    public void setUp() {
        viewDto = new CareerViewDto(
                careerId,
                LocalDate.of(2020, 9, 21),
                LocalDate.of(2025, 5, 21),
                "Company",
                "Engineer"
        );
    }

    @Test
    @DisplayName("POST - Успешное добавление эндпоинта карьеры")
    void addCareerTest() throws Exception {
        CareerCreateDto createDto = new CareerCreateDto(
                LocalDate.of(2020, 9, 21),
                LocalDate.of(2025, 5, 21),
                "Company",
                "Engineer"
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(careerService.career(userId, createDto)).thenReturn(viewDto);
        mockMvc.perform(post("/careers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));
        verify(careerService).career(userId, createDto);
    }

    @Test
    @DisplayName("PUT /careers/{careerId} — success")
    void updateCareerTest() throws Exception {
        long userId = 42L;
        long careerId = 1L;
        UpdateCareerDto updateDto = new UpdateCareerDto(
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 10, 19),
                "NewCompany",
                "Manager"
        );
        CareerViewDto viewDto = new CareerViewDto(
                careerId,
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 10, 19),
                "NewCompany",
                "Manager"
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(careerService.updateCareer(userId, careerId, updateDto)).thenReturn(viewDto);

        mockMvc.perform(put("/careers/{careerId}", careerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));
        verify(careerService).updateCareer(userId, careerId, updateDto);
    }

    @Test
    @DisplayName("GET /careers/{careerId} — success")
    void getCareerByIdTest() throws Exception {
        long careerId = 1L;
        CareerViewDto viewDto = new CareerViewDto(
                1L,
                LocalDate.of(2020, 9, 21),
                LocalDate.of(2025, 10, 21),
                "Company",
                "Engineer"
        );

        when(careerService.getById(careerId)).thenReturn(viewDto);

        mockMvc.perform(get("/careers/{careerId}", careerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Company"))
                .andExpect(jsonPath("$.position").value("Engineer"));

        verify(careerService).getById(careerId);
    }
}