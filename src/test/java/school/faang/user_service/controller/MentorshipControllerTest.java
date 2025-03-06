package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.util.UserGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {
    
    private static final Long USER_ID = 1L;
    private static final Long DELETE_USER_ID = 1L;
    private List<UserDto> userDtos;
    private MockMvc mockMvc;
    private UserGenerator userGenerator;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userGenerator = new UserGenerator();

        userDtos = Arrays.asList(
            userGenerator.generateUserDto(1L, "FirstUser"),
            userGenerator.generateUserDto(2L, "SecondUser")
        );
    }

    @Test
    @DisplayName("Get mentees success")
    void testGetMenteesSuccess() throws Exception {
        when(mentorshipService.getMentees(USER_ID)).thenReturn(userDtos);

        mockMvc.perform(get("/mentorship/users/{USER_ID}/mentees", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userGenerator.generateUserDto(1L, "FirstUser").getId()));

        verify(mentorshipService, times(1)).getMentees(USER_ID);
    }

    @Test
    @DisplayName("Get mentees failure - User not found")
    void testGetMenteesFail() throws Exception {
        doThrow(new EntityNotFoundException("User with ID " + USER_ID + " not found"))
                .when(mentorshipService).getMentees(USER_ID);

        mockMvc.perform(get("/mentorship/users/{USER_ID}/mentees", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID " + USER_ID + " not found"));
        verify(mentorshipService, times(1)).getMentees(USER_ID);
    }

    @Test
    @DisplayName("Get mentors success")
    void testGetMentorsSuccess() throws Exception {
        UserDto userDto = userGenerator.generateUserDto(1L, "FirstUser");
        when(mentorshipService.getMentors(USER_ID)).thenReturn(userDtos);

        mockMvc.perform(get("/mentorship/users/{USER_ID}/mentors", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()));

        verify(mentorshipService, times(1)).getMentors(USER_ID);
    }

    @Test
    @DisplayName("Get mentors failure - User not found")
    void testGetMentorsFail() throws Exception {
        when(mentorshipService.getMentors(USER_ID))
                .thenThrow(new EntityNotFoundException("User with ID " + USER_ID + " not found"));

        mockMvc.perform(get("/mentorship/users/{USER_ID}/mentors", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID " + USER_ID + " not found"));

        verify(mentorshipService, times(1)).getMentors(USER_ID);
    }

    @Test
    @DisplayName("Delete Mentee success")
    void testDeleteMenteeSuccess() throws Exception {
        doNothing().when(mentorshipService).deleteMentee(USER_ID, DELETE_USER_ID);

        mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", USER_ID, DELETE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMentee(USER_ID, DELETE_USER_ID);
    }

    @Test
    @DisplayName("Delete Mentee when mentor not found")
    void testDeleteMenteeWhenMentorNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User with ID " + DELETE_USER_ID + " not found"))
                .when(mentorshipService)
                .deleteMentee(USER_ID, DELETE_USER_ID);

        mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", USER_ID, DELETE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(mentorshipService).deleteMentee(USER_ID, DELETE_USER_ID);
    }

    @Test
    @DisplayName("Delete Mentor success")
    void testDeleteMentorSuccess() throws Exception {
        doNothing().when(mentorshipService).deleteMentor(USER_ID, DELETE_USER_ID);

        mockMvc.perform(delete("/mentorship/mentees/{menteeId}/mentors/{mentorId}", USER_ID, DELETE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMentor(USER_ID, DELETE_USER_ID);
    }

    @Test
    @DisplayName("Delete Mentor when mentor not found")
    void testDeleteMentorWhenMentorNotFound() throws Exception {
        String expectedErrorMessage = "User with ID " + DELETE_USER_ID + " not found";
        doThrow(new EntityNotFoundException(expectedErrorMessage))
                .when(mentorshipService).deleteMentor(USER_ID, DELETE_USER_ID);

        mockMvc.perform(delete("/mentorship/mentees/{menteeId}/mentors/{mentorId}", USER_ID, DELETE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedErrorMessage));

        verify(mentorshipService, times(1)).deleteMentor(USER_ID, DELETE_USER_ID);
    }
}
