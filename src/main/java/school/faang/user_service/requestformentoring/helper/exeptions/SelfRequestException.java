package school.faang.user_service.requestformentoring.helper.exeptions;

public class SelfRequestException extends RuntimeException {

    public SelfRequestException() {
        super("Пользователь не может отправить запрос сам себе");
    }
}
