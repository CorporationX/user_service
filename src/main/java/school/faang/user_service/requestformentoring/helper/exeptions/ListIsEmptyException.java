package school.faang.user_service.requestformentoring.helper.exeptions;

public class ListIsEmptyException extends RuntimeException {

    public ListIsEmptyException() {
        super("Список пуст.");
    }
}
