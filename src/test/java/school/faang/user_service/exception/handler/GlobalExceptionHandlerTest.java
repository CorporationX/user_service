package school.faang.user_service.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.TestRedisConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfiguration.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${services.user-service.name}")
    String serviceName;

    @Test
    void handleFileUploadException() throws Exception {
        mockMvc.perform(get("/file-upload-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.serviceName").value(serviceName))
                .andExpect(jsonPath("$.globalMessage").value("Something went wrong..."))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    void handleDataValidationException() throws Exception {
        mockMvc.perform(get("/data-validation-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceName").value(serviceName))
                .andExpect(jsonPath("$.globalMessage").value("Test exception"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        String usernameMessage = "Username cannot be blank",
                passwordMessage = "The password must be between 12 and 64 characters long",
                emailMessage = "Invalid email",
                countryIdMessage = "Country id cannot be null";

        var content = post("/method-argument-not-valid-exception")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"\", \"password\": \"short\", \"email\": \"invalid\", \"countryId\": null }");

        mockMvc.perform(content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").value(usernameMessage))
                .andExpect(jsonPath("$.fieldErrors.password").value(passwordMessage))
                .andExpect(jsonPath("$.fieldErrors.email").value(emailMessage))
                .andExpect(jsonPath("$.fieldErrors.countryId").value(countryIdMessage));
    }
}


