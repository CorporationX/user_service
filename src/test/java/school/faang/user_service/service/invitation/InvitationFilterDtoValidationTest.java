package school.faang.user_service.service.invitation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvitationFilterDtoValidationTest {

    private Validator validator;
    private InvitationFilterDto invitationFilterDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        invitationFilterDto = InvitationFilterDto.builder()
                .inviterNamePattern("SaraConnor")
                .invitedNamePattern("JohnConnor")
                .inviterId(1L)
                .invitedId(2L)
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Valid InvitationFilterDto")
    public void testValidInvitationFilterDto() {
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(invitationFilterDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("InviterNamePattern with blank space")
    public void testInvalidInviterNamePattern() {
        invitationFilterDto.setInviterNamePattern(" ");
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(invitationFilterDto);

        assertEquals(1, violations.size());
        assertEquals("inviterNamePattern must not be empty, but can be null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("InvitedNamePattern is empty")
    public void testInvalidInvitedNamePattern() {
        invitationFilterDto.setInvitedNamePattern("");
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(invitationFilterDto);

        assertEquals(1, violations.size());
        assertEquals("invitedNamePattern must not be empty, but can be null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("InviterId is negative")
    public void testInvalidInviterId() {
        invitationFilterDto.setInviterId(-1L);
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(invitationFilterDto);

        assertEquals(1, violations.size());
        assertEquals("inviterId must be a positive number or null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("InvitedId is zero")
    public void testInvalidInvitedId() {
        invitationFilterDto.setInvitedId(0L);
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(invitationFilterDto);

        assertEquals(1, violations.size());
        assertEquals("invitedId must be a positive number or null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Without filters")
    public void testNullFields() {
        InvitationFilterDto emptyFilterDto = InvitationFilterDto.builder()
                .inviterNamePattern(null)
                .invitedNamePattern(null)
                .inviterId(null)
                .invitedId(null)
                .status(null)
                .build();
        Set<ConstraintViolation<InvitationFilterDto>> violations = validator.validate(emptyFilterDto);

        assertTrue(violations.isEmpty());
    }
}
