package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.containsString;

//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//@WebAppConfiguration
@WebMvcTest(controllers = MentorshipRequestDto.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        MentorshipRequestDto invalidInputDto = MentorshipRequestDto.builder()
                .receiverId(1L)
                .requesterId(1L)
                .description(null)
                .status(null)
                .build();
        mockMvc.perform(post("api/v1//mentorship/request", invalidInputDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert that the error message is as expected
//        assertThat(result.getResponse().getContentAsString(), containsString("default message [must match"));
    }

    @Test
    void handleConstraintViolationException() {
    }

    @Test
    void handleDataValidationException() {
    }

    @Test
    void handleEntityNotFoundException() {
    }

    @Test
    void handleRuntimeException() {
    }

    @Test
    void handleException() {
    }
}