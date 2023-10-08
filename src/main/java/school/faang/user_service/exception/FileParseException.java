package school.faang.user_service.exception;

import java.io.IOException;

public class FileParseException extends RuntimeException {
    public FileParseException(String message, IOException e) {
        super(message);
    }
}
