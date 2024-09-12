package school.faang.user_service.service.invitation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalInvitationDtoValidationTest {

    private Validator validator;
    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedUserId(1L)
                .goalId(1L)
                .status(RequestStatus.PENDING).build();
    }

    @Test
    @DisplayName("InviterId is null")
    public void testInviterIdIsNull() {
        goalInvitationDto.setInviterId(null);
        Set<ConstraintViolation<GoalInvitationDto>> constraintViolations = validator.validate(goalInvitationDto);

        assertEquals(1, constraintViolations.size());

        ConstraintViolation<GoalInvitationDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals("inviterId can't be null", constraintViolation.getMessage());
    }

    @Test
    @DisplayName("InviterId is less than zero")
    public void testThatInviterIdIsLessThanZero() {
        goalInvitationDto.setInviterId(-1L);
        Set<ConstraintViolation<GoalInvitationDto>> constraintViolations = validator.validate(goalInvitationDto);

        assertEquals(1, constraintViolations.size());

        ConstraintViolation<GoalInvitationDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals("inviterId must be a positive number", constraintViolation.getMessage());
    }

    @Test
    @DisplayName("InviterId is zero")
    public void testThatInviterIdIsZero() {
        goalInvitationDto.setInviterId(0L);
        Set<ConstraintViolation<GoalInvitationDto>> constraintViolations = validator.validate(goalInvitationDto);

        assertEquals(1, constraintViolations.size());

        ConstraintViolation<GoalInvitationDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals("inviterId must be a positive number", constraintViolation.getMessage());
    }

    @Test
    @DisplayName("Status is null")
    public void testThatStatusIsNull() {
        goalInvitationDto.setStatus(null);
        Set<ConstraintViolation<GoalInvitationDto>> constraintViolations = validator.validate(goalInvitationDto);

        assertEquals(1, constraintViolations.size());

        ConstraintViolation<GoalInvitationDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals("RequestStatus can't be null", constraintViolation.getMessage());
    }
}
