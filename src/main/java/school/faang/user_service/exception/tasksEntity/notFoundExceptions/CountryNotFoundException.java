package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class CountryNotFoundException extends EntityNotFoundException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
