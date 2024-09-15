package school.faang.user_service.exception.s3;

public class FileUploadException extends RuntimeException{

    public FileUploadException(String message) {
        super(message);
    }
}
