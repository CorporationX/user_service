package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.UserValidator;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserValidator userValidator;

    private long validMentorId;
    private long invalidMentorId;
    private long validMenteeId;
    private long invalidMenteeId;

    @BeforeEach
    public void setUp() {
        validMentorId = 5L;
        invalidMentorId = -5L;
        validMenteeId = 10L;
        invalidMenteeId = -10L;
    }

    @Test
    public void testGetMentees() {
        // act
        mentorshipController.getMentees(validMentorId);

        // assert
        verify(mentorshipService).getMentees(validMentorId);
    }

    @Test
    public void testGetMenteesInvalidMentorId() {
        // arrange
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMentorId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.getMentees(invalidMentorId));
    }

    @Test
    public void testGetMentors() {
        // act
        mentorshipController.getMentors(validMenteeId);

        // assert
        verify(mentorshipService).getMentors(validMenteeId);
    }

    @Test
    public void testGetMentorsInvalidMentee() {
        // arrange
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMenteeId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.getMentors(invalidMenteeId));
    }

    @Test
    public void testDeleteMentee() {
        // act
        mentorshipController.deleteMentee(validMenteeId, validMentorId);

        // assert
        verify(mentorshipService).deleteMentee(validMenteeId, validMentorId);
    }

    @Test
    public void testDeleteMenteeInvalidMentorId() {
        // arrange
        doNothing().when(userValidator)
                .validateUserId(validMenteeId);
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMentorId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.deleteMentee(validMenteeId, invalidMentorId));
    }

    @Test
    public void testDeleteMenteeInvalidMenteeId() {
        // arrange
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMenteeId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.deleteMentee(invalidMenteeId, validMentorId));
    }

    @Test
    public void testDeleteMentor() {
        // act
        mentorshipController.deleteMentor(validMenteeId, validMentorId);

        // assert
        verify(mentorshipService).deleteMentor(validMenteeId, validMentorId);
    }

    @Test
    public void testDeleteMentorInvalidMentorId() {
        // arrange
        doNothing().when(userValidator)
                .validateUserId(validMenteeId);
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMentorId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.deleteMentor(validMenteeId, invalidMentorId));
    }

    @Test
    public void testDeleteMentorInvalidMenteeId() {
        // arrange
        doThrow(new IllegalArgumentException())
                .when(userValidator)
                .validateUserId(invalidMenteeId);

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipController.deleteMentor(invalidMenteeId, validMentorId));
    }
}