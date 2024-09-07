package school.faang.user_service.requestformentoring.helper.exeptions;


public class NotNullProvidedException extends RuntimeException {

    public <T> NotNullProvidedException(T type) {
        super(type + " не должен быть null");
    }
}
