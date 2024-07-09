package school.faang.user_service.dto;

public final class DtoValidationConstraints {

    private DtoValidationConstraints() {}

    public static final String VALIDATION_FAILED = "Validation failed.";
    public static final String MENTORSHIP_REQUEST_DESCRIPTION_CONSTRAINT = "Description is required.";
    public static final String MENTORSHIP_REQUEST_REQUESTER_ID_CONSTRAINT = "Requester ID is required.";
    public static final String MENTORSHIP_REQUEST_RECEIVER_ID_CONSTRAINT = "Receiver ID is required.";
}
