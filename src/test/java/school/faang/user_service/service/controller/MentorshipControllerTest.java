package school.faang.user_service.service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    @DisplayName("Trying to return list of mentees")
    void testGetMentees_ShouldReturnsListMenteesDto() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.getMentees(null));
    }

    @Test
    @DisplayName("Trying to return list of mentors")
    void testGetMentees_ShouldReturnsListMentorsDto() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.getMentors(null));
    }
}
