package school.faang.user_service.controller.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = TestController.class)
@Import(UserContext.class)
@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testMethodArgumentNotValidHandling() throws Exception {
        mockMvc.perform(post("/test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/validate"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.name").value("must not be blank"))
                .andExpect(jsonPath("$.details.aboutMe").value("must not be null"));
    }

    @Test
    void testBadInputHandlingForIllegalArg() throws Exception {
        mockMvc.perform(get("/test/illegal/argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/illegal/argument"))
                .andExpect(jsonPath("$.message").value("Bad input value"));
    }

    @Test
    void testBadInputHandlingForIllegalState() throws Exception {
        mockMvc.perform(get("/test/illegal/state"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/illegal/state"))
                .andExpect(jsonPath("$.message").value("Incorrect mentoring request data"));
    }

    @Test
    void testDataValidationExceptionHandling() throws Exception {
        mockMvc.perform(get("/test/data/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/data/validation"))
                .andExpect(jsonPath("$.message")
                        .value("experience min or experience max cannot be less than zero"));
    }

    @Test
    void testEntityNotFoundExceptionHandling() throws Exception {
        mockMvc.perform(get("/test/entity"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.url").value("/test/entity"))
                .andExpect(jsonPath("$.message").value("It seems this user does not exist"));
    }

    @Test
    void testForbiddenExceptionHandling() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.url").value("/test/forbidden"))
                .andExpect(jsonPath("$.message")
                        .value("user is trying to change someone else's data"));
    }

    @Test
    void testUnexpectedErrorHandling() throws Exception {
        mockMvc.perform(get("/test/null"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.url").value("/test/null"))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}