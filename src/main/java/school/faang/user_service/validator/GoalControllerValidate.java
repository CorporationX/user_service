package school.faang.user_service.validator;

public class GoalControllerValidate {

    public void validateId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
    }
}