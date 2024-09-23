package school.faang.user_service.controller.goal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.service.GoalInvitationService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService service;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    private Validator validator;
    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        goalInvitationDto = new GoalInvitationDto(null, 1L, 1L, 1L, null);
    }

    @Test
    void testCreateInvitation_positive() {
        goalInvitationController.createInvitation(goalInvitationDto);

        Mockito.verify(service).createInvitation(goalInvitationDto);
    }

    @Test
    void testCreateInvitation_inviterIdIsNull() {
        goalInvitationDto.setInviterId(null);

        Set<ConstraintViolation<GoalInvitationDto>> violations = validator.validate(goalInvitationDto, GoalInvitationDto.BeforeCreate.class);
        goalInvitationController.createInvitation(goalInvitationDto);


        assertEquals(1, violations.size());
        assertEquals("не должно равняться null", violations.iterator().next().getMessage());
    }

    @Test
    void testCreateInvitation_invitedUserIdIsNull() {
        goalInvitationDto.setInvitedUserId(null);

        Set<ConstraintViolation<GoalInvitationDto>> violations = validator.validate(goalInvitationDto, GoalInvitationDto.BeforeCreate.class);
        goalInvitationController.createInvitation(goalInvitationDto);

        assertEquals(1, violations.size());
        assertEquals("не должно равняться null", violations.iterator().next().getMessage());
    }

    @Test
    void testCreateInvitation_goalIdIsNull() {
        goalInvitationDto.setGoalId(null);

        Set<ConstraintViolation<GoalInvitationDto>> violations = validator.validate(goalInvitationDto, GoalInvitationDto.BeforeCreate.class);
        goalInvitationController.createInvitation(goalInvitationDto);

        assertEquals(1, violations.size());
        assertEquals("не должно равняться null", violations.iterator().next().getMessage());
    }

    @Test
    void testGetInvitations_positive() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInvitedId(1L);

        goalInvitationController.getInvitations(filterDto);

        Mockito.verify(service).getInvitations(filterDto);
    }

    @Test
    void testGetInvitations_InvitationFilterDtoIsNull() {
        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationController.getInvitations(null));
        assertEquals("InvitationFilterDto must not be null", exception.getMessage());
    }

    @Test
    void testGetInvitations_InvitationFilterDtoWithAllFieldsAreNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationController.getInvitations(filterDto));
        assertEquals("At least one of field InvitationFilterDto must not be null", exception.getMessage());
    }
}