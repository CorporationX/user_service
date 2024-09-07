package school.faang.user_service.requestformentoring.helper.exeptions;

public class MentorshipRequestNotFoundException extends RuntimeException {

    public MentorshipRequestNotFoundException() {
        super("Запрос не найден. Для начала нужно создать запрос.");
    }
}
