package school.faang.user_service.exception.user;

import java.io.IOException;

public class FileUploadedException extends RuntimeException {
    public FileUploadedException(String message, IOException e) {
        super(message, e);
    }
}
