package school.faang.user_service.exception;

import lombok.Getter;
import school.faang.user_service.valitator.Error;

import java.util.List;

public class DataValidationException extends RuntimeException {


    @Getter
    private final List<Error> errors;

    public DataValidationException(List<Error> errors) {
        this.errors = errors;
    }
}