package school.faang.user_service.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${services.user-service.name}")
    String serviceName;

    private final String message = "Test exception";

    @Test
    void handleFileUploadException() throws Exception {
        mockMvc.perform(get("/file-upload-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.serviceName").value(serviceName))
                .andExpect(jsonPath("$.globalMessage").value(message))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    void handleDataValidationException() throws Exception {
        mockMvc.perform(get("/data-validation-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serviceName").value(serviceName))
                .andExpect(jsonPath("$.globalMessage").value(message))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
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
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()").value(4))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'username' && @.message == '" + usernameMessage + "')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password' && @.message == '" + passwordMessage + "')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'email' && @.message == '" + emailMessage + "')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'countryId' && @.message == '" + countryIdMessage + "')]").exists());
    }
}


