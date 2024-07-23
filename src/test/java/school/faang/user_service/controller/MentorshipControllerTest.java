package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    private long mentorId;
    private long menteeId;
    private List<UserDto> menteesDto;
    private List<UserDto> mentorsDto;

    @BeforeEach
    public void setup() {
        mentorId = 1L;
        menteeId = 2L;

        menteesDto = new ArrayList<>();
        mentorsDto = new ArrayList<>();

        reset(mentorshipService);
    }

    @Test
    @DisplayName("testGetMentees")
    public void testGetMentees() {
        when(mentorshipService.getMentees(mentorId)).thenReturn(menteesDto);

        List<UserDto> result = mentorshipController.getMentees(mentorId);

        verify(mentorshipService).getMentees(mentorId);
        assertNotNull(result);
        assertEquals(menteesDto, result);
    }

    @Test
    @DisplayName("testGetMentors")
    public void testGetMentors() {
        when(mentorshipService.getMentors(menteeId)).thenReturn(mentorsDto);

        List<UserDto> result = mentorshipController.getMentors(menteeId);

        verify(mentorshipService).getMentors(menteeId);
        assertNotNull(result);
        assertEquals(mentorsDto, result);
    }

    @Test
    @DisplayName("testDeleteMentee")
    public void testDeleteMentee() {
        mentorshipController.deleteMentee(menteeId, mentorId);

        verify(mentorshipService).deleteMentee(menteeId, mentorId);
    }

    @Test()
    @DisplayName("testDeleteMentor")
    public void testDeleteMentor() {
        mentorshipController.deleteMentor(menteeId, mentorId);

        verify(mentorshipService).deleteMentor(menteeId, mentorId);
    }
}
