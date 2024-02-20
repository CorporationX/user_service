package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubscriptionController.class)
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionValidator subscriptionValidator;

    @MockBean
    private UserContext userContext;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new SubscriptionController(subscriptionService, subscriptionValidator, userContext))
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    public void testDataValidationException() throws Exception {
        given(subscriptionService.getFollowingCount(1L)).willThrow(new DataValidationException("Validation error"));

        mockMvc.perform(get("/user/1/followees/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Validation error\"}"));
    }

    @Test
    public void testEntityNotFoundException() throws Exception {
        given(subscriptionService.getFollowersCount(1L)).willThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/user/1/followers/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"User not found\"}"));
    }

    @Test
    public void testRuntimeException() throws Exception {
        given(subscriptionService.getFollowingCount(1L)).willThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/user/1/followees/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("{\"message\":\"Internal error\"}"));
    }
    @Test
    public void testUserFilterDtoFakeValidation() throws Exception {
        String requestBody = "{\"emailPattern\":\"123\"}";

        mockMvc.perform(post("/user/1/followers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.emailPattern").value("Некорректный email"));
    }
}
