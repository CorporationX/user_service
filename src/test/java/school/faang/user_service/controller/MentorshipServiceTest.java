package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void getMentees_CorrectAnswerThrows() {
        assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(0));

    }

    @ParameterizedTest
    @CsvSource({"2"})
    public void getMentees_CorrectAnswer(long id) {
        mentorshipService.getMentees(id);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(id);
    }
}