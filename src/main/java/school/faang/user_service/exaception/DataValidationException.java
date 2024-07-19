package school.faang.user_service.exaception;

public class DataValidationException extends IllegalArgumentException{

    public DataValidationException(String s) {
        super(s);
    }
}
