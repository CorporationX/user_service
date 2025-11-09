package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipController.class)
public class MentorshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentorshipService mentorshipService;

    @MockBean
    private UserContext userContext;

    @Test
    void testAddMentorshipWhenValidRequestThenReturnsCreated() throws Exception {
        long mentorId = 1L;
        long menteeId = 2L;

        when(userContext.getUserId()).thenReturn(mentorId);

        mockMvc.perform(post("/mentorship/{mentorId}/{menteeId}", mentorId, menteeId))
                .andExpect(status().isCreated());

        verify(mentorshipService, times(1)).addMentorship(mentorId, menteeId);
    }

    @Test
    void testAddMentorshipWhenUserNotAuthorizedThenThrowsForbiddenException() throws Exception {
        long mentorId = 1L;
        long menteeId = 2L;
        long currentUserId = 999L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        mockMvc.perform(post("/mentorship/{mentorId}/{menteeId}", mentorId, menteeId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddMentorshipWhenMentorIdEqualsMenteeIdThenThrowsDataValidationException() throws Exception {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(post("/mentorship/{mentorId}/{menteeId}", userId, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteMentorshipWhenValidRequestThenReturnsNoContent() throws Exception {
        long mentorId = 1L;
        long menteeId = 2L;

        when(userContext.getUserId()).thenReturn(mentorId);

        mockMvc.perform(delete("/mentorship/{mentorId}/{menteeId}", mentorId, menteeId))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMentorship(mentorId, menteeId);
    }

    @Test
    void testDeleteMentorshipWhenUserNotAuthorizedThenThrowsForbiddenException() throws Exception {
        long mentorId = 1L;
        long menteeId = 2L;
        long currentUserId = 999L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        mockMvc.perform(delete("/mentorship/{mentorId}/{menteeId}", mentorId, menteeId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteMentorshipWhenMentorIdEqualsMenteeIdThenThrowsDataValidationException() throws Exception {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/mentorship/{mentorId}/{menteeId}", userId, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMenteesWhenValidRequestThenReturnsList() throws Exception {
        long userId = 1L;

        List<UserDto> mentees = List.of(
                new UserDto(2L, "user1", "email1@test.com", "123456", "About me 1", 5, List.of()),
                new UserDto(3L, "user2", "email2@test.com", "789012", "About me 2", 3, List.of())
        );

        when(mentorshipService.getMentees(userId)).thenReturn(mentees);

        mockMvc.perform(get("/mentorship/mentee/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));

        verify(mentorshipService, times(1)).getMentees(userId);
    }

    @Test
    void testGetMentorsWhenValidRequestThenReturnsList() throws Exception {
        long userId = 1L;

        List<UserDto> mentors = List.of(
                new UserDto(2L, "mentor1", "mentor1@test.com", "111111", "About mentor 1", 10, List.of()),
                new UserDto(3L, "mentor2", "mentor2@test.com", "222222", "About mentor 2", 15, List.of())
        );

        when(mentorshipService.getMentors(userId)).thenReturn(mentors);

        mockMvc.perform(get("/mentorship/mentor/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));

        verify(mentorshipService, times(1)).getMentors(userId);
    }
}