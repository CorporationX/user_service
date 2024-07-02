package school.faang.user_service.exception;

public class CommonException extends RuntimeException {

    private final OperationStatus operationStatus;

    public CommonException(String errorMessage, OperationStatus status) {
        super(errorMessage);
        operationStatus = status;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }
}
