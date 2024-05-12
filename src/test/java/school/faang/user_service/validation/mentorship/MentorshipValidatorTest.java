package school.faang.user_service.validation.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipValidatorTest {
    User firstMentee;
    User secondMentor;


    @InjectMocks
    private MentorshipValidator mentorshipValidator;
    @BeforeEach
    public void init() {
        firstMentee = User.builder()
                .id(11L)
                .build();
        secondMentor = User.builder()
                .id(2L)
                .build();
    }

    @Test
    void addGoalToMenteeFromMentorValidationTestMentorIsNotOnList(){
        firstMentee.setMentors(List.of());
        assertThrows(IllegalArgumentException.class,()-> mentorshipValidator.addGoalToMenteeFromMentorValidation(firstMentee,secondMentor));
    }
    @Test
    void addGoalToMenteeFromMentorValidationTestMenteeDoesNotMentors() {
        assertThrows(NullPointerException.class,()-> mentorshipValidator.addGoalToMenteeFromMentorValidation(firstMentee,secondMentor));
    }
    @Test
    void addGoalToMenteeFromMentorValidationTest(){
        firstMentee.setMentors(List.of(secondMentor));
        assertDoesNotThrow(()->mentorshipValidator.addGoalToMenteeFromMentorValidation(firstMentee,secondMentor));
    }
}