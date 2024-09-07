package school.faang.user_service.requestformentoring.helper.exeptions;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Пользователь не найден.");
    }
}
