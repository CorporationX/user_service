package school.faang.user_service.service;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        System.out.println(message);
    }
}
