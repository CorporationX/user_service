package school.faang.user_service.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleFileUploadException() throws Exception {
        mockMvc.perform(get("/file-upload-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong..."));
    }

    @Test
    void handleDataValidationException() throws Exception {
        mockMvc.perform(get("/data-validation-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        not(emptyOrNullString())
                ));
    }
}


